package campionebase.erosionera.client.screen;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.api.IBioCamera;
import campionebase.erosionera.api.IBioControllable;
import campionebase.erosionera.api.IBioObservable;
import campionebase.erosionera.inventory.BioControllerMenu;
import campionebase.erosionera.network.BioCameraHelper;
import campionebase.erosionera.network.BioCameraManager;
import campionebase.erosionera.network.BioMachineryNetwork;
import campionebase.erosionera.network.packet.BioCameraAlivePacket;
import campionebase.erosionera.network.packet.BioCameraListPacket;
import campionebase.erosionera.registry.ErErKeyBindings;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.List;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(
        value = Dist.CLIENT,
        modid = ErosionEra.MODID
)
public class BioControllerScreen extends Screen implements MenuAccess<BioControllerMenu> {
    private final BioControllerMenu menu;
    private final Level level;
    private long windowHandle;
    public BioControllerScreen(BioControllerMenu menu, Inventory inventory, Component title) {
        super(title);
        this.menu = menu;
        this.level = inventory.player.level();
    }

    @Override
    public @NotNull BioControllerMenu getMenu() {
        return this.menu;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // 绘制一层深色背景，遮挡游戏世界（类似工作台背景效果）
        if (this.menu.getSelectedIndex() == -1)
            graphics.fill(0, 0, this.width, this.height, 0xC0101010);
        this.renderCameraList(graphics);
        this.renderTarget(graphics);
    }

    private void renderCameraList(GuiGraphics graphics) {
        int x = 10;
        int y = this.height - 20; // 从底部往上绘制
        int lineHeight = 10;

        // 先绘制所有条目（倒着画，从下往上）
        List<BioControllerMenu.CameraInfo> cameras = this.menu.getCameras();
        int selected = this.menu.getSelectedIndex();

        // 主视角条目（索引 -1）
        graphics.drawString(this.font, (selected == -1) ? "-> Main View" : "Main View",
                x, y - lineHeight,
                (selected == -1) ? 0xffffffff : 0xffaaaaaa, false);
        y -= lineHeight + 5;

        // 摄像机条目（从上往下，顺序与列表一致，但绘制从底部往上累加，所以倒序遍历）
        for (int i = cameras.size() - 1; i >= 0; i--) {
            BioControllerMenu.CameraInfo info = cameras.get(i);
            String display = (selected == i) ? "-> " : info.username() == null ? "" : "[" + info.username() + "] ";
            graphics.drawString(this.font, display + info.camera().getName(),
                    x, y - lineHeight,
                    (selected == i) ? 0xffffffff : 0xffaaaaaa, false);
            y -= lineHeight + 2;
        }
    }

    private void renderTarget(GuiGraphics graphics){
        IBioCamera camera = this.menu.getCamera();
        if (camera == null) return;
        BlockHitResult result = BioCameraHelper.pickBlock(this.level, camera.getBlockPos(), this.menu.cameraYaw, this.menu.cameraPitch);
        if (result.getType() == HitResult.Type.MISS) return;
        BlockState blockState = level.getBlockState(result.getBlockPos());
        if (!(blockState.getBlock() instanceof IBioObservable.BlockSource observable)) return;
        String info = observable.getInfo(blockState).getString();

        int x = this.width / 2 + 15;
        int y = this.height / 2 + 5;
        graphics.drawString(this.font, info, x, y, 0xffffffff, false);
    }

    @Override
    protected void init() {
        super.init();
        if (this.minecraft != null) {
            // 向服务器请求数据
            BioMachineryNetwork.INSTANCE.sendToServer(new BioCameraListPacket.Request(this.menu.getBlockPos()));
            this.windowHandle = this.minecraft.getWindow().getWindow();
            //this.minecraft.mouseHandler.grabMouse();
            this.grabMouse();
        }
    }

    private int tickCount = 0;
    @Override
    public void tick() {
        super.tick();
        IBioCamera camera = this.menu.getCamera();
        if (camera == null) return;

        this.tickCount ++;
        if (this.tickCount % BioCameraManager.UPDATE_TICK_INTERVAL == 0){
            BioMachineryNetwork.LOGGER.debug("[Health] Send bio-camera[{}]`s beat", camera.getBlockPos());
            BioMachineryNetwork.INSTANCE.sendToServer(new BioCameraAlivePacket(
                    camera.getBlockPos(),
                    this.menu.cameraYaw, this.menu.cameraPitch)
            );
        }


    }

    @Override
    public void removed() {
        if (this.minecraft != null) {
            this.releaseMouse();
        }
        this.menu.exit();
        super.removed();
    }

    public void grabMouse(){
        if (this.minecraft == null) return;
        double x = this.minecraft.getWindow().getGuiScaledWidth() * 0.5;
        double y = this.minecraft.getWindow().getGuiScaledHeight() * 0.5;
        InputConstants.grabOrReleaseMouse(this.windowHandle, GLFW.GLFW_CURSOR_HIDDEN, x, y);
    }

    public void releaseMouse(){
        if (this.minecraft == null) return;
        double x = this.minecraft.getWindow().getGuiScaledWidth() * 0.5;
        double y = this.minecraft.getWindow().getGuiScaledHeight() * 0.5;
        InputConstants.grabOrReleaseMouse(this.windowHandle, GLFW.GLFW_CURSOR_NORMAL, x, y);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == ErErKeyBindings.PREV_BIO_CAMERA_KEY.getKey().getValue()){
            this.menu.selectPrev();
            return true;
        }
        if (keyCode == ErErKeyBindings.NEXT_BIO_CAMERA_KEY.getKey().getValue()){
            this.menu.selectNext();
            return true;
        }
        if (keyCode == ErErKeyBindings.BIO_CONTROL_UP.getKey().getValue()) {
            this.menu.action(IBioControllable.ControlAction.INCREMENT);
        }
        if (keyCode == ErErKeyBindings.BIO_CONTROL_DOWN.getKey().getValue()) {
            this.menu.action(IBioControllable.ControlAction.DECREMENT);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (this.minecraft == null) return;
        if (this.minecraft.isWindowActive()) {
            // 将鼠标拉回中心
            GLFW.glfwSetCursorPos(this.windowHandle,
                    this.minecraft.getWindow().getScreenWidth() * 0.5,
                    this.minecraft.getWindow().getScreenHeight() * 0.5);
        }
        IBioCamera camera = this.menu.getCamera();
        if (camera == null) return;

        double dx = mouseX - this.minecraft.getWindow().getGuiScaledWidth() * 0.5;
        double dy = mouseY - this.minecraft.getWindow().getGuiScaledHeight() * 0.5;

        float sens = (float) (this.minecraft.options.sensitivity().get() * 0.6F);
        float scale = 0.8F; // 视角旋转系数
        this.menu.cameraYaw += (float) (dx * scale * sens);
        this.menu.cameraPitch += (float) (dy * scale * sens);

        this.menu.cameraPitch = Mth.clamp(this.menu.cameraPitch, camera.getMinPitch(), camera.getMaxPitch());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int keyCode) {
        if (keyCode == GLFW.GLFW_MOUSE_BUTTON_LEFT){

        } else if (keyCode == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {

        }

        return super.mouseClicked(mouseX, mouseY, keyCode);
    }


}
