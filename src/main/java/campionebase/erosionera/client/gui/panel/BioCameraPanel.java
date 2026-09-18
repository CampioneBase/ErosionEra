package campionebase.erosionera.client.gui.panel;

import campionebase.erosionera.api.BioMachineData;
import campionebase.erosionera.api.BioMachineType;
import campionebase.erosionera.api.IBioCamera;
import campionebase.erosionera.blockentity.BioCameraBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class BioCameraPanel extends AbstractBioMachinePanel<IBioCamera> {
    public static final String CURSOR = "-> ";
    public static final int VISIBLE_ENTRY_COUNT = 5;
    private static final int ROW_GAP = 2;

    public BioCameraPanel(BioMachineType<IBioCamera> type, Screen screen) {
        super(type, screen);
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;
        int pointX = this.x;
        int cursorWidth = font.width(CURSOR);
        int rowStep = getRowStep();

        int visibleCount = getVisibleEntryCount();
        if (visibleCount > 0) {
            int start = Mth.clamp(this.selectedIndex - 2, 0,
                    Math.max(0, this.entries.size() - visibleCount));
            int end = Math.min(start + visibleCount, this.entries.size());

            for (int index = start; index < end; index++) {
                int row = index - start;
                int pointY = this.y + row * rowStep;
                boolean selected = index == this.selectedIndex;
                int color = selected ? 0xffffffff : 0xffc0c0c0;

                if (selected) {
                    graphics.drawString(font, CURSOR, pointX, pointY, color);
                }

                BioMachineData data = this.entries.get(index);
                String displayName = data.customData().getString(BioCameraBlockEntity.TAG_NAME);
                if (data.customData().contains(BioCameraBlockEntity.TAG_OCCUPIER)) {
                    displayName += data.customData().getString(BioCameraBlockEntity.TAG_OCCUPIER);
                }
                graphics.drawString(font, displayName, pointX + cursorWidth + 4, pointY, color);
            }
        }

        int mainViewRow = getRowCount() - 1;
        int mainViewY = this.y + mainViewRow * rowStep;
        int mainViewColor = this.selectedIndex == -1 ? 0xffffffff : 0xffc0c0c0;
        if (this.selectedIndex == -1) {
            graphics.drawString(font, CURSOR, pointX, mainViewY, mainViewColor);
        }
        graphics.drawString(font, "Main View", pointX + cursorWidth + 4, mainViewY, mainViewColor);
    }

    public int getContentHeight() {
        return getRowCount() * getRowStep() - ROW_GAP;
    }

    private int getVisibleEntryCount() {
        if (this.selectedIndex < 0 || this.entries.isEmpty()) return 0;
        return Math.min(VISIBLE_ENTRY_COUNT, this.entries.size());
    }

    private int getRowCount() {
        return 1 + getVisibleEntryCount();
    }

    private static int getRowStep() {
        return Minecraft.getInstance().font.lineHeight + ROW_GAP;
    }

    @Override
    protected IBioCamera getInstanceAt(Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof IBioCamera camera)
            return camera;
        return null;
    }
}
