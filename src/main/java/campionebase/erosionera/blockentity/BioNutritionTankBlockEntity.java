package campionebase.erosionera.blockentity;

import campionebase.erosionera.api.BioMachineType;
import campionebase.erosionera.api.IBioMachine;
import campionebase.erosionera.registry.BioMachineTypes;
import campionebase.erosionera.registry.ErErBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BioNutritionTankBlockEntity extends BlockEntity implements IBioMachine {
    public BioNutritionTankBlockEntity(BlockPos pos, BlockState blockState) {
        super(ErErBlockEntities.BIO_NUTRITION_TANK.get(), pos, blockState);
    }

    @Override
    public @NotNull BioMachineType<? extends IBioMachine> getMachineType() {
        return BioMachineTypes.NUTRITION_TANK.get();
    }
}
