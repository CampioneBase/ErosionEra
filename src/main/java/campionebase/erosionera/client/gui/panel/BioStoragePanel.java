package campionebase.erosionera.client.gui.panel;

import campionebase.erosionera.api.BioMachineType;
import campionebase.erosionera.api.IBioStorage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class BioStoragePanel extends AbstractBioMachinePanel<IBioStorage>{

    public BioStoragePanel(BioMachineType<IBioStorage> type, Screen screen) {
        super(type, screen);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected @Nullable IBioStorage getInstanceAt(Level level, BlockPos pos) {
        return null;
    }
}
