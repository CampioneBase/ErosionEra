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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class CameraPanel extends AbstractBioMachinePanel<IBioCamera> {
    public static final String CURSOR = "-> ";

    public CameraPanel(BioMachineType<IBioCamera> type, Screen screen) {
        super(type, screen);
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public void handleUpdate(BioMachineData update) {
        this.updateData(update, (oldNbt, newNbt) -> newNbt);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY) {
        Font font = Minecraft.getInstance().font;
        int pointX = this.x, pointY = this.y; // 左下角为锚点
        int lineHeight = font.lineHeight;
        int cursorWidth = font.width(CURSOR);
        // 主视角
        if (this.selectedIndex == -1) {
            graphics.drawString(font, CURSOR, pointX, pointY, 0xfffcfcfc);
        }
        graphics.drawString(font, "Main View", pointX + cursorWidth + 4, pointY, 0xfffcfcfc);
        pointY -= lineHeight + 4;
        // 列表
        for (int i = 0; i < this.entries.size(); i++) {
            CompoundTag data = this.entries.get(i).customData();
            if (this.selectedIndex == i){
                graphics.drawString(font, CURSOR, pointX, pointY, 0xfffcfcfc);
            }
            String displayName = data.getString(BioCameraBlockEntity.TAG_NAME);
            if (data.contains(BioCameraBlockEntity.TAG_OCCUPIER)){
                displayName += data.getString(BioCameraBlockEntity.TAG_OCCUPIER);
            }
            graphics.drawString(font, displayName, pointX + cursorWidth + 4, pointY, 0xfffcfcfc);
            pointY -= lineHeight + 4;
        }
    }

    @Override
    protected IBioCamera getInstanceAt(Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof IBioCamera camera)
            return camera;
        return null;
    }

    @Override
    protected boolean isSelectionValid(int index) {
        CompoundTag tag = this.entries.get(index).customData();
        return !tag.contains(BioCameraBlockEntity.TAG_OCCUPIER);
    }

}
