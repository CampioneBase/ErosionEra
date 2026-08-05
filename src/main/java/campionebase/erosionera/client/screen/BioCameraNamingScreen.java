package campionebase.erosionera.client.screen;

import campionebase.erosionera.blockentity.BioCameraBlockEntity;
import campionebase.erosionera.data.lang.Translation;
import campionebase.erosionera.data.lang.TranslationKeys;
import campionebase.erosionera.network.BioMachineryNetwork;
import campionebase.erosionera.network.packet.BioCameraNamingPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

@Translation
public class BioCameraNamingScreen extends Screen {
    @Translation.ZH_CN("命名活体摄像机")
    public static final String TRANSLATION_KEY_TITLE = TranslationKeys.getKeyName("bio_camera_naming", "screen", "title");

    private final BioCameraBlockEntity camera;
    private EditBox nameField;
    private final String originalName;

    public BioCameraNamingScreen(BioCameraBlockEntity camera) {
        super(Component.translatable(TRANSLATION_KEY_TITLE));
        this.camera = camera;
        this.originalName = camera.getName();
    }

    @Override
    protected void init() {
        super.init();
        // 输入框
        this.nameField = new EditBox(this.font, this.width / 2 - 100, this.height / 2 - 10, 200, 20, Component.literal("Name"));
        this.nameField.setValue(this.originalName != null ? this.originalName : "");
        this.nameField.setMaxLength(32);
        this.addWidget(this.nameField);

        // 确认按钮
        this.addRenderableWidget(Button.builder(Component.translatable(TranslationKeys.BUTTON_CONFIRM), btn -> {
            this.confirm();
            this.onClose();
        }).bounds(this.width / 2 - 100, this.height / 2 + 20, 60, 20).build());

        // 重置按钮
        this.addRenderableWidget(Button.builder(Component.translatable(TranslationKeys.BUTTON_RESET), btn -> this.reset())
                .bounds(this.width / 2 - 30, this.height / 2 + 20, 60, 20).build());

        // 取消按钮
        this.addRenderableWidget(Button.builder(Component.translatable(TranslationKeys.BUTTON_CANCEL), btn -> this.onClose())
                .bounds(this.width / 2 + 40, this.height / 2 + 20, 60, 20).build());

        this.setInitialFocus(this.nameField);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics); // 半透明背景
        String title = Component.translatable(TRANSLATION_KEY_TITLE).getString();
        graphics.drawCenteredString(this.font, title, this.width / 2, this.height / 2 - 30, 0xFFFFFF);
        this.nameField.render(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER) {
            // 按回车触发确认
            this.confirm();
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void confirm(){
        BioMachineryNetwork.INSTANCE.sendToServer(new BioCameraNamingPacket(
                this.camera.getBlockPos(),
                this.nameField.getValue().trim()
        ));
        // this.camera.setName(this.nameField.getValue().trim());
    }

    private void reset(){
        this.nameField.setValue(camera.getDefaultName());
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
