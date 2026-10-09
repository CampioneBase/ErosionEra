package campionebase.erosionera.api.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public final class BioMachineRenderers {
    private BioMachineRenderers(){}
    //region ===================== 默认值 =====================
    public static final int NORMAL_COLOR = 0xFFAAAAAA;
    public static final int SELECTED_COLOR = 0xFFFAFAFA;

    public static final int BAR_BACKGROUND_COLOR = 0xF0333333;
    //endregion
    //region ===================== 文本 =====================

    /** 单行文本，默认灰色，行高 10 */
    public static BioMachineRenderable text(Component text) {
        return text(() -> text, NORMAL_COLOR);
    }

    /** 单行文本，指定颜色 */
    public static BioMachineRenderable text(Component text, int color) {
        return text(() -> text, color);
    }

    /** 可变单行文本，动态提供。行高 10 */
    public static BioMachineRenderable text(Supplier<Component> textSupplier, int color) {
        return new TextLine(textSupplier, color, 10);
    }

    /** 选中时变色的单行文本。行高 10 */
    public static BioMachineRenderable selectedText(Component text, int normalColor, int selectedColor) {
        return selectedText(() -> text, normalColor, selectedColor, 10);
    }

    /** 选中时变色的单行文本 */
    public static BioMachineRenderable selectedText(Component text, int normalColor, int selectedColor, int lineHeight) {
        return selectedText(() -> text, normalColor, selectedColor, lineHeight);
    }

    /** 选中时变色的可变单行文本 */
    public static BioMachineRenderable selectedText(Supplier<Component> textSupplier, int normalColor, int selectedColor, int lineHeight) {
        return new SelectableTextLine(textSupplier, normalColor, selectedColor, lineHeight);
    }

    //endregion
    //region ===================== 进度条 =====================

    /** 纯色进度条，高度 6，深灰底 */
    public static BioMachineRenderable progressBar(float ratio, int fillColor) {
        return new ProgressBar(() -> ratio, fillColor, BAR_BACKGROUND_COLOR, 6, null);
    }

    /** 指定高度的纯色进度条 */
    public static BioMachineRenderable progressBar(float ratio, int fillColor, int height) {
        return new ProgressBar(() -> ratio, fillColor, BAR_BACKGROUND_COLOR, height, null);
    }

    /** 带居中标语的进度条，高度 12 */
    public static BioMachineRenderable labeledProgressBar(float ratio, int fillColor, Component label) {
        return new ProgressBar(() -> ratio, fillColor, BAR_BACKGROUND_COLOR, 12, () -> label);
    }

    /** 动态 ratio 的进度条 */
    public static BioMachineRenderable progressBar(Supplier<Float> ratioSupplier, int fillColor) {
        return new ProgressBar(ratioSupplier, fillColor, BAR_BACKGROUND_COLOR, 6, null);
    }

    //endregion
    //region ===================== 组合 =====================

    /** 垂直布局，默认间距 2 */
    public static BioMachineRenderable vbox(BioMachineRenderable... children) {
        return new VBox(List.of(children), 2);
    }

    /** 垂直布局，指定间距 */
    public static BioMachineRenderable vbox(int spacing, BioMachineRenderable... children) {
        return new VBox(List.of(children), spacing);
    }

    /** 垂直布局，默认间距 2 */
    public static BioMachineRenderable vbox(List<BioMachineRenderable> children) {
        return new VBox(List.copyOf(children), 2);
    }

    /** 垂直布局，指定间距 */
    public static BioMachineRenderable vbox(int spacing, List<BioMachineRenderable> children){
        return new VBox(List.copyOf(children), spacing);
    }

    //endregion
    //region --------------------- 基础实现 ---------------------

    // 单行文本
    private record TextLine(Supplier<Component> textSupplier, int color, int lineHeight)
            implements BioMachineRenderable
    {
        @Override
        public void render(GuiGraphics graphics,
                           int x, int y, int width, int height,
                           boolean selected, float partialTick) {
            Font font = Minecraft.getInstance().font;
            graphics.drawString(font, this.textSupplier.get(), x, y, this.color, false);
        }

        @Override
        public int preferredHeight() {
            return this.lineHeight;
        }
    }
    // 选中变色文本
    private record SelectableTextLine(Supplier<Component> textSupplier, int normalColor, int selectedColor, int lineHeight)
            implements BioMachineRenderable
    {
        @Override
        public void render(GuiGraphics graphics,
                           int x, int y, int width, int height,
                           boolean selected, float partialTick) {
            Font font = Minecraft.getInstance().font;
            int color = selected ? this.selectedColor : this.normalColor;
            graphics.drawString(font, this.textSupplier.get(), x, y, color, false);
        }

        @Override
        public int preferredHeight() {
            return this.lineHeight;
        }
    }

    // 进度条
    private record ProgressBar(Supplier<Float> ratioSupplier,
                               int fillColor, int backgroundColor,
                               int barHeight, @Nullable Supplier<Component> labelSupplier)
            implements BioMachineRenderable {

        @Override
        public void render(GuiGraphics graphics, int x, int y, int width, int height,
                           boolean selected, float partialTick) {
            float ratio = Mth.clamp(this.ratioSupplier.get(), 0f, 1f);
            int barY = y + (height - this.barHeight) / 2;

            // 背景
            graphics.fill(x, barY, x + width, barY + this.barHeight, this.backgroundColor);
            // 前景
            int fillWidth = Math.round(width * ratio);
            graphics.fill(x, barY, x + fillWidth, barY + this.barHeight, this.fillColor);

            // 居中标语
            if (this.labelSupplier != null) {
                Font font = Minecraft.getInstance().font;
                Component label = this.labelSupplier.get();
                int textWidth = font.width(label);
                int textX = x + (width - textWidth) / 2;
                int textY = y + (height - font.lineHeight) / 2;
                graphics.drawString(font, label, textX, textY, 0xFFFFFF, true);
            }
        }

        @Override
        public int preferredHeight() {
            return this.barHeight;
        }
    }

    // 垂直布局
    private record VBox(List<BioMachineRenderable> children, int spacing)
            implements BioMachineRenderable
    {
        @Override
        public void render(GuiGraphics graphics, int x, int y, int width, int height,
                           boolean selected, float partialTick) {
            int childY = y;
            for (BioMachineRenderable child : this.children) {
                int childHeight = child.preferredHeight();
                child.render(graphics, x, childY, width, childHeight, selected, partialTick);
                childY += childHeight + this.spacing;
            }
        }

        @Override
        public int preferredHeight() {
            int total = 0;
            for (int i = 0; i < this.children.size(); i++) {
                total += this.children.get(i).preferredHeight();
                if (i < this.children.size() - 1) total += this.spacing;
            }
            return total;
        }
    }
    //endregion
}
