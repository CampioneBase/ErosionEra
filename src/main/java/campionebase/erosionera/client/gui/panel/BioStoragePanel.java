package campionebase.erosionera.client.gui.panel;

import campionebase.erosionera.api.BioMachineType;
import campionebase.erosionera.api.BioMachineData;
import campionebase.erosionera.api.BioMachineUpdate;
import campionebase.erosionera.api.IBioStorage;
import campionebase.erosionera.api.render.BioMachineRenderable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class BioStoragePanel extends AbstractBioMachinePanel<IBioStorage>{
    private static final int ROW_GAP = 4;

    public BioStoragePanel(BioMachineType<IBioStorage> type, Screen screen) {
        super(type, screen);
    }

    @Override
    public void refreshData(List<BioMachineData> dataList) {
        super.refreshData(dataList);
        this.rebuildRenderers();
    }

    @Override
    public void handleUpdate(BioMachineUpdate update) {
        super.handleUpdate(update);
        this.rebuildRenderers();
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (this.renderers.size() != this.entries.size()) {
            this.rebuildRenderers();
        }

        int renderY = this.y;
        for (int index = 0; index < this.renderers.size(); index++) {
            BioMachineRenderable renderable = this.renderers.get(index);
            int rowHeight = renderable.preferredHeight();
            if (renderY + rowHeight > this.y + this.height) break;

            renderable.render(graphics, this.x, renderY, this.width, rowHeight,
                    index == this.selectedIndex, partialTick);
            renderY += rowHeight + ROW_GAP;
        }
    }

    @Override
    protected @Nullable IBioStorage getInstanceAt(Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof IBioStorage storage ? storage : null;
    }

    private void rebuildRenderers() {
        this.renderers.clear();
        for (BioMachineData data : this.entries) {
            this.renderers.add(PanelRenderers.create(data.customData()));
        }
    }
}
