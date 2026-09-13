package campionebase.erosionera.client.screen;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.api.*;
import campionebase.erosionera.client.gui.panel.AbstractBioMachinePanel;
import campionebase.erosionera.client.gui.panel.BioMachinePanels;
import campionebase.erosionera.client.gui.panel.CameraControllerPanel;
import campionebase.erosionera.inventory.BioControllerMenu;
import campionebase.erosionera.network.BioCameraManager;
import campionebase.erosionera.network.BioMachineryNetwork;
import campionebase.erosionera.network.packet.c2s.BioCameraAlivePacket;
import campionebase.erosionera.network.packet.rr.BioCameraOccupationPacket;
import campionebase.erosionera.network.packet.rr.BioMachineListPacket;
import campionebase.erosionera.registry.BioMachineTypes;
import campionebase.erosionera.registry.ErErKeyBindings;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.*;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(
        value = Dist.CLIENT,
        modid = ErosionEra.MODID
)
public class BioControllerScreen extends Screen implements MenuAccess<BioControllerMenu> {
    private final SortedMap<BioMachineType<?>, AbstractBioMachinePanel<?>> panels = new TreeMap<>();
    private int lastDataVersion = -1;
    private final CameraControllerPanel controller;

    private final BioControllerMenu menu;
    private final Level level;
    private long windowHandle;
    public BioControllerScreen(BioControllerMenu menu, Inventory inventory, Component title) {
        super(title);
        this.menu = menu;
        this.level = inventory.player.level();
        this.controller = new CameraControllerPanel(this, menu);
    }

    @Override
    protected void init() {
        super.init();
        this.panels.put(BioMachineTypes.CAMERA.get(), this.controller); // 必有面板
        this.controller.x = 16;
        this.controller.y = this.height - 16;
        this.refreshPanels();
        if (this.minecraft != null) {
            this.windowHandle = this.minecraft.getWindow().getWindow();
            IBioCore core = this.menu.getCore();
            if (core != null) {
                // 向服务器请求数据
                BioMachineryNetwork.INSTANCE.sendToServer(new BioMachineListPacket.Request(core.getBlockPos()));
            }
            //this.minecraft.mouseHandler.grabMouse();
            this.grabMouse();
        }
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
        this.updatePanels();
        // 绘制一层深色背景，遮挡游戏世界（类似工作台背景效果）
        if (this.controller.getSelectedIndex() == -1)
            graphics.fill(0, 0, this.width, this.height, 0xC0101010);
        this.renderPanels(graphics, mouseX, mouseY);
    }

    private void updatePanels(){
        // 检查同步
        if (this.lastDataVersion != this.menu.getDataVersion()){
            this.refreshPanels();
            this.lastDataVersion = this.menu.getDataVersion();
        }
        // 检查更新
        this.menu.drainUpdateQueue(update -> {
            AbstractBioMachinePanel<?> panel = this.panels.get(update.type());
            if (panel == null) return;
            panel.handleUpdate(update);
        });
    }

    private void renderPanels(GuiGraphics graphics, int mouseX, int mouseY){
        this.panels.values().forEach(panel -> {
            if (!panel.isVisible()) return;
            panel.render(graphics, mouseX, mouseY);
        });
    }

    private void refreshPanels(){
        if (this.level == null) return;
        this.panels.values().forEach(AbstractBioMachinePanel::clear);
        // 拉取同步数据
        Set<BioMachineData> dataSet = this.menu.getDataSet();
        Map<AbstractBioMachinePanel<?>, List<BioMachineData>> cache = new LinkedHashMap<>();
        // 按照类型分类
        dataSet.forEach(data -> {
            if (!(this.level.getBlockEntity(data.pos()) instanceof IBioMachine machine)) return;
            BioMachineType<?> type = machine.getMachineType();
            AbstractBioMachinePanel<?> panel = this.panels.get(type);
            // 还未创建对应面板
            if (panel == null) {
                if (!BioMachinePanels.hasPanel(type)) return;
                panel = BioMachinePanels.createPanel(type, this);
                // 未注册对应面板时直接跳过，不影响已有面板同步
                if (panel == null) return;
                this.panels.put(type, panel);
            }
            cache.computeIfAbsent(panel, ignored -> new LinkedList<>()).add(data);
        });
        // 写入面板
        cache.forEach(AbstractBioMachinePanel::refreshData);
    }

    public void onCameraOccupationResponse(BioCameraOccupationPacket.ResultState state, @Nullable BlockPos cameraPos){
        switch (state) {
            case SUCCESS -> {
                if (cameraPos == null) {
                    this.controller.confirmSelecting(null);
                    return;
                }
                if (this.level.getBlockEntity(cameraPos) instanceof IBioCamera camera) {
                    this.controller.confirmSelecting(camera);
                }
            }
            case OCCUPIED -> {

            }
            case INVALID -> {

            }
        }

    }

    private int tickCount = 0;
    @Override
    public void tick() {
        super.tick();
        this.tickCount ++;
        IBioCamera camera = this.menu.getMachine();
        IBioController controller = this.menu.getController();



        if (this.tickCount % BioCameraManager.UPDATE_TICK_INTERVAL == 0){
            if (camera != null && controller != null){
                BioMachineryNetwork.LOGGER.trace("[Health] Send bio-camera[{}]`s beat", camera.getBlockPos());
                BioMachineryNetwork.INSTANCE.sendToServer(new BioCameraAlivePacket(
                        camera.getBlockPos(), this.menu.getController().getBlockPos(),
                        this.menu.cameraYaw, this.menu.cameraPitch)
                );
            }
        }
    }

    @Override
    public void removed() {
        this.releaseMouse();
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
            this.controller.selectPrev();
            return true;
        }
        if (keyCode == ErErKeyBindings.NEXT_BIO_CAMERA_KEY.getKey().getValue()){
            this.controller.selectNext();
            return true;
        }
        if (keyCode == ErErKeyBindings.BIO_CONTROL_UP.getKey().getValue()) {
            this.menu.action(IBioController.Action.INCREMENT);
            return true;
        }
        if (keyCode == ErErKeyBindings.BIO_CONTROL_DOWN.getKey().getValue()) {
            this.menu.action(IBioController.Action.DECREMENT);
            return true;
        }
        if (keyCode == ErErKeyBindings.BIO_CONTROL_MARK.getKey().getValue()) {
            this.menu.action(IBioController.Action.MARK);
            return true;
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
        IBioCamera camera = this.menu.getMachine();
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
        if (keyCode == GLFW.GLFW_MOUSE_BUTTON_LEFT || keyCode == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            this.menu.requestPick(keyCode == GLFW.GLFW_MOUSE_BUTTON_RIGHT);
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, keyCode);
    }
}
