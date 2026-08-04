package campionebase.erosionera.client.screen;

import campionebase.erosionera.api.IBioCamera;
import campionebase.erosionera.inventory.BioControllerMenu;
import campionebase.erosionera.network.BioMachineryNetwork;
import campionebase.erosionera.network.packet.UpdateBioCameraListPacket;
import campionebase.erosionera.registry.ErErKeyBindings;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class BioControllerScreen extends Screen implements MenuAccess<BioControllerMenu> {
    private final BioControllerMenu menu;
    private long windowHandle;
    public BioControllerScreen(BioControllerMenu menu, Inventory inventory, Component title) {
        super(title);
        this.menu = menu;
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
            graphics.drawString(this.font, display + info.camera().getName().getString(),
                    x, y - lineHeight,
                    (selected == i) ? 0xffffffff : 0xffaaaaaa, false);
            y -= lineHeight + 2;
        }
    }

    @Override
    protected void init() {
        super.init();
        if (this.minecraft != null) {
            // 向服务器请求数据
            BioMachineryNetwork.INSTANCE.sendToServer(new UpdateBioCameraListPacket.Request(this.menu.getBlockPos()));
            this.windowHandle = this.minecraft.getWindow().getWindow();
            //this.minecraft.mouseHandler.grabMouse();
            this.grabMouse();
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
        InputConstants.grabOrReleaseMouse(this.windowHandle, GLFW.GLFW_CURSOR_DISABLED, x, y);
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
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (this.minecraft == null) return;
        // 将鼠标拉回中心
        GLFW.glfwSetCursorPos(this.windowHandle,
                this.minecraft.getWindow().getScreenWidth() * 0.5,
                this.minecraft.getWindow().getScreenHeight() * 0.5);
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
}
