package campionebase.erosionera.block.entity;

import campionebase.erosionera.registry.ErErBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BioNutritionTankBlockEntity extends BioMachineBlockEntity {
    public BioNutritionTankBlockEntity(BlockPos pos, BlockState blockState) {
        super(ErErBlockEntities.BIO_NUTRITION_TANK.get(), pos, blockState);
    }

    @Override
    public boolean isCore() {
        return false;
    }
}
