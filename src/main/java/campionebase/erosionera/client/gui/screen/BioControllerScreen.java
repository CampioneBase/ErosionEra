package campionebase.erosionera.client.gui.screen;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.api.*;
import campionebase.erosionera.client.gui.layout.PanelLayout;
import campionebase.erosionera.client.gui.panel.AbstractBioMachinePanel;
import campionebase.erosionera.client.gui.panel.BioCameraControllerPanel;
import campionebase.erosionera.client.gui.panel.BioMachinePanels;
import campionebase.erosionera.inventory.BioControllerMenu;
import campionebase.erosionera.network.BioCameraManager;
import campionebase.erosionera.network.BioMachineryNetwork;
import campionebase.erosionera.network.packet.c2s.BioCameraAlivePacket;
import campionebase.erosionera.network.packet.rr.BioCameraOccupationPacket;
import campionebase.erosionera.network.packet.rr.BioMachineListPacket;
import campionebase.erosionera.registry.BioMachineTypes;
import campionebase.erosionera.registry.ErErItems;
import campionebase.erosionera.registry.ErErKeyBindings;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(
        value = Dist.CLIENT,
        modid = ErosionEra.MODID
)
public class BioControllerScreen extends Screen implements MenuAccess<BioControllerMenu> {
    private static final int LEFT_SECTION_WIDTH = 144;
    private static final int PANEL_PADDING = 12;
    private static final int CONTENT_GAP = 8;
    private static final int LIST_ROW_HEIGHT = 14;
    private static final int BACKGROUND_COLOR = 0x101010;
    private static final int BACKGROUND_ALPHA = 0xc0;
    private static final float PANEL_FADE_SECONDS = 0.12F;
    private static final float LIST_COLLAPSE_SECONDS = 0.18F;
    private static final float PANEL_SLIDE_DISTANCE = 36.0F;

    private final SortedMap<BioMachineType<?>, AbstractBioMachinePanel<?>> panels = new TreeMap<>();
    private final BioCameraControllerPanel controller;
    private final BioControllerMenu menu;
    private final Level level;
    private final ItemStack cameraIcon;

    private @Nullable BioMachineType<?> selectedType;
    private List<BioMachineType<?>> visibleTypes = List.of();
    private int lastDataVersion = -1;
    private int leftDividerY = PANEL_PADDING;
    private long windowHandle;
    private long lastRenderTime = Util.getMillis();
    private float rightPanelVisibility = 1.0F;
    private float listCollapseProgress;
    private boolean mouseGrabbed;
    private int tickCount;

    public BioControllerScreen(BioControllerMenu menu, Inventory inventory, Component title) {
        super(title);
        this.menu = menu;
        this.level = inventory.player.level();
        this.controller = new BioCameraControllerPanel(this, menu);
        this.cameraIcon = new ItemStack(ErErItems.BIO_CAMERA.get());
    }

    @Override
    protected void init() {
        super.init();
        this.controller.open();
        this.panels.putIfAbsent(BioMachineTypes.CAMERA.get(), this.controller);
        this.refreshPanels();
        if (this.menu.getMachine() != null) {
            this.controller.confirmSelecting(this.menu.getMachine());
        }
        this.updateVisibleTypes();
        this.ensureSelectedType();
        this.layoutController();

        boolean controlling = this.controller.getSelectedIndex() != -1;
        this.rightPanelVisibility = controlling ? 0.0F : 1.0F;
        this.listCollapseProgress = controlling ? 1.0F : 0.0F;
        this.lastRenderTime = Util.getMillis();
        this.lastDataVersion = this.menu.getDataVersion();

        if (this.minecraft != null) {
            this.windowHandle = this.minecraft.getWindow().getWindow();
            this.releaseMouse();
            this.updateMouseMode();

            IBioCore core = this.menu.getCore();
            if (core != null) {
                BioMachineryNetwork.INSTANCE.sendToServer(new BioMachineListPacket.Request(core.getBlockPos()));
            }
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
        this.updateAnimations();
        this.layoutController();

        float cameraProgress = 1.0F - this.rightPanelVisibility;
        int backgroundAlpha = Math.round(BACKGROUND_ALPHA * (1.0F - cameraProgress));
        if (backgroundAlpha > 0) {
            graphics.fill(0, 0, this.width, this.height,
                    backgroundAlpha << 24 | BACKGROUND_COLOR);
        }

        this.renderTypeList(graphics);
        this.renderRightPanel(graphics, mouseX, mouseY, partialTick);
        this.controller.render(graphics, mouseX, mouseY, partialTick);
    }

    private void updatePanels() {
        if (this.lastDataVersion != this.menu.getDataVersion()) {
            this.refreshPanels();
            this.lastDataVersion = this.menu.getDataVersion();
        }

        this.menu.drainUpdateQueue(update -> {
            AbstractBioMachinePanel<?> panel = this.panels.get(update.type());
            if (panel != null) {
                panel.handleUpdate(update);
            }
        });

        this.updateVisibleTypes();
        this.ensureSelectedType();
        this.updateMouseMode();
    }

    private void refreshPanels() {
        if (this.level == null) return;

        Collection<AbstractBioMachinePanel<?>> inactivePanels = new LinkedHashSet<>(this.panels.values());
        Set<BioMachineData> dataSet = this.menu.getDataSet();
        Map<AbstractBioMachinePanel<?>, List<BioMachineData>> cache = new LinkedHashMap<>();

        dataSet.forEach(data -> {
            if (!(this.level.getBlockEntity(data.pos()) instanceof IBioMachine machine)) return;
            BioMachineType<?> type = machine.getMachineType();
            AbstractBioMachinePanel<?> panel = this.panels.get(type);
            if (panel == null) {
                if (!BioMachinePanels.hasPanel(type)) return;
                panel = BioMachinePanels.createPanel(type, this);
                if (panel == null) return;
                this.panels.put(type, panel);
            }
            inactivePanels.remove(panel);
            cache.computeIfAbsent(panel, ignored -> new LinkedList<>()).add(data);
        });

        cache.forEach((panel, dataList) -> {
            panel.open();
            panel.refreshData(dataList);
        });
        inactivePanels.forEach(panel -> {
            if (panel != this.controller) {
                panel.close();
            }
        });

        this.updateVisibleTypes();
        this.ensureSelectedType();
    }

    private void updateVisibleTypes() {
        this.visibleTypes = this.panels.entrySet().stream()
                .filter(entry -> entry.getValue() != this.controller)
                .filter(entry -> entry.getValue().isVisible() && !entry.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.toList());
    }

    private void ensureSelectedType() {
        if (this.selectedType != null && this.visibleTypes.contains(this.selectedType)) {
            return;
        }
        this.selectedType = this.visibleTypes.isEmpty() ? null : this.visibleTypes.get(0);
    }

    private void updateAnimations() {
        long now = Util.getMillis();
        float deltaSeconds = Math.min((now - this.lastRenderTime) / 1000.0F, 0.1F);
        this.lastRenderTime = now;

        boolean controlling = this.controller.getSelectedIndex() != -1;
        float cameraTarget = controlling ? 1.0F : 0.0F;
        this.rightPanelVisibility = approach(this.rightPanelVisibility,
                1.0F - cameraTarget, deltaSeconds / PANEL_FADE_SECONDS);
        this.listCollapseProgress = approach(this.listCollapseProgress,
                cameraTarget, deltaSeconds / LIST_COLLAPSE_SECONDS);
    }

    private static float approach(float current, float target, float amount) {
        if (current < target) {
            return Math.min(target, current + amount);
        }
        return Math.max(target, current - amount);
    }

    private static float easeOutCubic(float value) {
        float inverse = 1.0F - Mth.clamp(value, 0.0F, 1.0F);
        return 1.0F - inverse * inverse * inverse;
    }

    private void layoutController() {
        this.controller.width = Math.max(1, LEFT_SECTION_WIDTH - PANEL_PADDING * 2);
        this.controller.height = this.controller.getContentHeight();
        this.controller.x = PANEL_PADDING;
        this.controller.y = Math.max(PANEL_PADDING,
                this.height - PANEL_PADDING - this.controller.height);

        this.leftDividerY = Math.max(PANEL_PADDING + LIST_ROW_HEIGHT,
                this.controller.y - CONTENT_GAP);
    }

    private void renderTypeList(GuiGraphics graphics) {
        Font font = Minecraft.getInstance().font;
        float collapse = easeOutCubic(this.listCollapseProgress);

        if (collapse < 1.0F) {
            graphics.enableScissor(0, 0, LEFT_SECTION_WIDTH, this.leftDividerY);
            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F - collapse);

            int x = PANEL_PADDING - Math.round(collapse * LEFT_SECTION_WIDTH);
            int y = PANEL_PADDING;
            for (BioMachineType<?> type : this.visibleTypes) {
                if (y + font.lineHeight > this.leftDividerY) break;

                boolean selected = type.equals(this.selectedType);
                Component text = selected
                        ? Component.literal("选项：" + type.getDescriptionId())
                        : Component.translatable(type.getDescriptionId());
                int color = selected ? 0xffffffff : 0xffa7b0ad;
                graphics.drawString(font, text, x, y, color, true);
                y += LIST_ROW_HEIGHT;
            }

            graphics.flush();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            graphics.disableScissor();
        }

        if (collapse > 0.0F) {
            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, collapse);
            graphics.renderItem(this.cameraIcon, PANEL_PADDING, PANEL_PADDING);
            graphics.flush();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    private void renderRightPanel(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (this.rightPanelVisibility <= 0.001F) return;

        AbstractBioMachinePanel<?> panel = this.layoutSelectedPanel();
        if (panel == null || !panel.isVisible()) return;

        float gone = easeOutCubic(1.0F - this.rightPanelVisibility);
        int slide = Math.round(gone * PANEL_SLIDE_DISTANCE);

        graphics.pose().pushPose();
        graphics.pose().translate(slide, 0.0F, 0.0F);
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.rightPanelVisibility);
        panel.render(graphics, mouseX, mouseY, partialTick);
        graphics.flush();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.pose().popPose();
    }

    private @Nullable AbstractBioMachinePanel<?> layoutSelectedPanel() {
        if (this.selectedType == null) return null;
        AbstractBioMachinePanel<?> panel = this.panels.get(this.selectedType);
        if (panel == null || !panel.isVisible()) return null;

        int x = LEFT_SECTION_WIDTH + PANEL_PADDING;
        int y = PANEL_PADDING;
        int width = Math.max(0, this.width - x - PANEL_PADDING);
        int height = Math.max(0, this.height - y - PANEL_PADDING);
        panel.width = width;
        panel.height = height;

        PanelLayout layout = new PanelLayout(x, y, width, height);
        layout.addChild(panel);
        layout.arrangeElements();
        return panel;
    }

    public void onCameraOccupationResponse(BioCameraOccupationPacket.ResultState state, @Nullable BlockPos cameraPos) {
        switch (state) {
            case SUCCESS -> {
                if (cameraPos == null) {
                    this.controller.confirmSelecting(null);
                    this.updateMouseMode();
                    return;
                }
                if (this.level.getBlockEntity(cameraPos) instanceof IBioCamera camera) {
                    this.controller.confirmSelecting(camera);
                    this.updateMouseMode();
                }
            }
            case OCCUPIED, INVALID -> {
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.tickCount++;
        IBioCamera camera = this.menu.getMachine();
        IBioController controller = this.menu.getController();
        if (this.tickCount % BioCameraManager.UPDATE_TICK_INTERVAL == 0) {
            if (camera != null && controller != null) {
                BioMachineryNetwork.LOGGER.trace("[Health] Send bio-camera[{}]`s beat", camera.getBlockPos());
                BioMachineryNetwork.INSTANCE.sendToServer(new BioCameraAlivePacket(
                        camera.getBlockPos(), controller.getBlockPos(),
                        this.menu.cameraYaw, this.menu.cameraPitch)
                );
            }
        }
    }

    @Override
    public void removed() {
        this.releaseMouse();
        this.mouseGrabbed = false;
        this.menu.exit();
        super.removed();
    }

    private void updateMouseMode() {
        this.setMouseGrabbed(this.controller.getSelectedIndex() != -1);
    }

    private void setMouseGrabbed(boolean grabbed) {
        if (this.minecraft == null || this.windowHandle == 0L || this.mouseGrabbed == grabbed) {
            return;
        }
        if (grabbed) {
            this.grabMouse();
        } else {
            this.releaseMouse();
        }
        this.mouseGrabbed = grabbed;
    }

    private void grabMouse() {
        if (this.minecraft == null) return;
        double x = this.minecraft.getWindow().getGuiScaledWidth() * 0.5;
        double y = this.minecraft.getWindow().getGuiScaledHeight() * 0.5;
        InputConstants.grabOrReleaseMouse(this.windowHandle, GLFW.GLFW_CURSOR_HIDDEN, x, y);
    }

    private void releaseMouse() {
        if (this.minecraft == null || this.windowHandle == 0L) return;
        double x = this.minecraft.getWindow().getGuiScaledWidth() * 0.5;
        double y = this.minecraft.getWindow().getGuiScaledHeight() * 0.5;
        InputConstants.grabOrReleaseMouse(this.windowHandle, GLFW.GLFW_CURSOR_NORMAL, x, y);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == ErErKeyBindings.PREV_BIO_CAMERA_KEY.getKey().getValue()) {
            this.controller.selectPrev();
            return true;
        }
        if (keyCode == ErErKeyBindings.NEXT_BIO_CAMERA_KEY.getKey().getValue()) {
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
        if (this.minecraft == null || this.controller.getSelectedIndex() == -1) return;
        if (this.minecraft.isWindowActive()) {
            GLFW.glfwSetCursorPos(this.windowHandle,
                    this.minecraft.getWindow().getScreenWidth() * 0.5,
                    this.minecraft.getWindow().getScreenHeight() * 0.5);
        }

        IBioCamera camera = this.menu.getMachine();
        if (camera == null) return;

        double dx = mouseX - this.minecraft.getWindow().getGuiScaledWidth() * 0.5;
        double dy = mouseY - this.minecraft.getWindow().getGuiScaledHeight() * 0.5;

        float sens = (float) (this.minecraft.options.sensitivity().get() * 0.6F);
        float scale = 0.8F;
        this.menu.cameraYaw += (float) (dx * scale * sens);
        this.menu.cameraPitch += (float) (dy * scale * sens);
        this.menu.cameraPitch = Mth.clamp(this.menu.cameraPitch,
                camera.getMinPitch(), camera.getMaxPitch());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int keyCode) {
        if (this.controller.getSelectedIndex() == -1) {
            if (keyCode == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                BioMachineType<?> type = this.getTypeAt(mouseX, mouseY);
                if (type != null) {
                    this.selectedType = type;
                    return true;
                }
            }
            return super.mouseClicked(mouseX, mouseY, keyCode);
        }

        if (keyCode == GLFW.GLFW_MOUSE_BUTTON_LEFT || keyCode == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            this.menu.requestPick(keyCode == GLFW.GLFW_MOUSE_BUTTON_RIGHT);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, keyCode);
    }

    private @Nullable BioMachineType<?> getTypeAt(double mouseX, double mouseY) {
        if (this.listCollapseProgress > 0.5F
                || mouseX < PANEL_PADDING
                || mouseX > LEFT_SECTION_WIDTH - PANEL_PADDING
                || mouseY < PANEL_PADDING
                || mouseY >= this.leftDividerY) {
            return null;
        }

        int index = (int) ((mouseY - PANEL_PADDING) / LIST_ROW_HEIGHT);
        if (index < 0 || index >= this.visibleTypes.size()) return null;
        return this.visibleTypes.get(index);
    }
}
