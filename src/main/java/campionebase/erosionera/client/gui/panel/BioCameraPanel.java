package campionebase.erosionera.client.gui.panel;

import campionebase.erosionera.api.BioMachineData;
import campionebase.erosionera.api.BioMachineType;
import campionebase.erosionera.api.IBioCamera;
import campionebase.erosionera.api.IBioMachine;
import campionebase.erosionera.api.render.BioMachineRenderers;
import campionebase.erosionera.blockentity.BioCameraBlockEntity;
import campionebase.erosionera.client.gui.animation.AnimatedInteger;
import campionebase.erosionera.client.gui.animation.Easing;
import campionebase.erosionera.data.lang.TranslationKeys;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class BioCameraPanel extends AbstractBioMachinePanel<IBioCamera> {
    // 选项标记
    private static final int MAIN_MARKER_NONE = 0;
    private static final int MAIN_MARKER_TOP = -1;
    private static final int MAIN_MARKER_BOTTOM = 1;
    // 动画时长
    private static final float ANIMATION_TIME = 0.6F;
    // 显示最大行数
    public static final int VISIBLE_ENTRY_COUNT = 5;
    // 行间距
    private static final int ROW_GAP = 2;
    // 主视角名称
    private static final Component MAIN_TEXT = Component.translatable(TranslationKeys.BIO_CAMERA_MAIN);
    // 总体限制高度
    private final AnimatedInteger animatedHeight = new AnimatedInteger(this.lineHeight(), ANIMATION_TIME, Easing.EASE_OUT_CUBIC);
    // 列表内容相对面板顶部的偏移
    private final AnimatedInteger animatedOffsetY = new AnimatedInteger(0, ANIMATION_TIME, Easing.EASE_OUT_CUBIC);
    // 逻辑选项 -1 当前锚定在哪一个主视角标记
    private boolean isOnTop = true;
    // 等待异步选择确认时，-1 应当落在哪一个主视角标记上
    private int pendingMainMarker = MAIN_MARKER_NONE;

    public BioCameraPanel(BioMachineType<IBioCamera> type, Screen screen) {
        super(type, screen);
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    private int lineHeight() {
        return Minecraft.getInstance().font.lineHeight + ROW_GAP;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float deltaSeconds) {
        this.animatedOffsetY.advance(deltaSeconds);
        this.animatedHeight.advance(deltaSeconds);
        Font font = Minecraft.getInstance().font;

        int offsetY = this.animatedOffsetY.value();
        this.height = this.animatedHeight.value();

        graphics.enableScissor(this.x, this.y, this.x + this.width, this.y + this.height);
        /* --- 绘制列表 ---
         * Main View （-1 Top）
         * Camera 0
         * Camera 1
         * ......
         * Camera size - 1
         * Main View  （size Bottom）
         * --------------- */
        int rowCount = this.entries.size() + 2;
        for (int row = 0; row < rowCount; row++) {
            int pointY = this.y + offsetY + this.lineHeight() * row;

            if (row == 0 || row == rowCount - 1) {
                // 主视角
                boolean isTop = row == 0;
                boolean selected = this.selectedIndex == -1 && isTop == this.isOnTop;
                int color = selected ? BioMachineRenderers.SELECTED_COLOR : BioMachineRenderers.NORMAL_COLOR;
                graphics.drawString(font, MAIN_TEXT, this.x, pointY, color, false);
            }
            else {
                // 摄像机
                int index = row - 1;
                boolean selected = this.selectedIndex == index;
                int color = selected ? BioMachineRenderers.SELECTED_COLOR : BioMachineRenderers.NORMAL_COLOR;
                BioMachineData data = this.entries.get(index);
                String name = IBioMachine.getDisplayName(data.customData());
                if (data.customData().contains(BioCameraBlockEntity.TAG_OCCUPIER, Tag.TAG_STRING)){
                    name += " " + data.customData().getString(BioCameraBlockEntity.TAG_OCCUPIER);
                }
                graphics.drawString(font, name, this.x, pointY, color, false);
            }
        }
        graphics.disableScissor();
    }

    @Override
    public void selectNext() {
        // 从底部主视角越界时，先瞬移到顶部主视角。
        if (this.selectedIndex == -1 && !this.isOnTop) {
            this.isOnTop = true;
            this.animatedOffsetY.snapTo(-this.lineHeight() * this.mainMarkerRow());
        }
        this.pendingMainMarker = MAIN_MARKER_BOTTOM;
        super.selectNext();
    }

    @Override
    public void selectPrev() {
        // 从顶部主视角越界时，先瞬移到底部主视角。
        if (this.selectedIndex == -1 && this.isOnTop) {
            this.isOnTop = false;
            this.animatedOffsetY.snapTo(-this.lineHeight() * this.mainMarkerRow());
        }
        this.pendingMainMarker = MAIN_MARKER_TOP;
        super.selectPrev();
    }

    @Override
    protected void select(int index) {
        super.select(index);
        if (this.selectedIndex == -1 && this.pendingMainMarker != MAIN_MARKER_NONE) {
            this.isOnTop = this.pendingMainMarker == MAIN_MARKER_TOP;
            this.pendingMainMarker = MAIN_MARKER_NONE;
        } else if (this.selectedIndex != -1) {
            this.pendingMainMarker = MAIN_MARKER_NONE;
        }

        if (this.selectedIndex == -1) {
            // 折叠
            this.animatedHeight.setTarget(this.lineHeight());
        } else {
            // 展开
            this.animatedHeight.setTarget(this.lineHeight() * this.realVisualLine());
        }
        this.animatedOffsetY.setTarget(-this.lineHeight() * this.scrollRow());
    }

    private int scrollRow() {
        if (this.selectedIndex == -1) {
            return this.mainMarkerRow();
        }

        int rowCount = this.entries.size() + 2;
        int selectedRow = this.selectedIndex + 1;
        int centeredRow = selectedRow - this.realVisualLine() / 2;
        int maxScrollRow = Math.max(0, rowCount - this.realVisualLine());
        return Mth.clamp(centeredRow, 0, maxScrollRow);
    }

    private int mainMarkerRow() {
        return this.isOnTop ? 0 : this.entries.size() + 1;
    }

    private int realVisualLine(){
        if (this.entries.isEmpty()){
            return 1;
        }
        return Math.min(this.entries.size() + 2, VISIBLE_ENTRY_COUNT);
    }

    @Override
    protected IBioCamera getInstanceAt(Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof IBioCamera camera)
            return camera;
        return null;
    }
}
