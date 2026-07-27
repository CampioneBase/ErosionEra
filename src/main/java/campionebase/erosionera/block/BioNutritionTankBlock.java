package campionebase.erosionera.block;

import campionebase.erosionera.block.entity.BioNutritionTankBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BioNutritionTankBlock extends Block implements EntityBlock {
    public BioNutritionTankBlock() {
        super(BlockBehaviour.Properties.of()

        );
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        return new BioNutritionTankBlockEntity(pos, blockState);
    }
}
