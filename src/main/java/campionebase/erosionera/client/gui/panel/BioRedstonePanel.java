package campionebase.erosionera.client.gui.panel;

import campionebase.erosionera.api.BioMachineType;
import campionebase.erosionera.blockentity.BioRedstoneBlockEntity;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class BioRedstonePanel extends AbstractBioMachinePanel<BioRedstoneBlockEntity> {
    public BioRedstonePanel(BioMachineType<BioRedstoneBlockEntity> type, Screen screen) {
        super(type, screen);
    }

    @Override
    protected @Nullable BioRedstoneBlockEntity getInstanceAt(Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof BioRedstoneBlockEntity redstone ? redstone : null;
    }
}
