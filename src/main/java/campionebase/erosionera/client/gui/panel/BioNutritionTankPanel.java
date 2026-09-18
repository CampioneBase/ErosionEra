package campionebase.erosionera.client.gui.panel;

import campionebase.erosionera.api.BioMachineType;
import campionebase.erosionera.blockentity.BioNutritionTankBlockEntity;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class BioNutritionTankPanel extends AbstractBioMachinePanel<BioNutritionTankBlockEntity> {
    public BioNutritionTankPanel(BioMachineType<BioNutritionTankBlockEntity> type, Screen screen) {
        super(type, screen);
    }

    @Override
    protected @Nullable BioNutritionTankBlockEntity getInstanceAt(Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof BioNutritionTankBlockEntity tank ? tank : null;
    }
}
