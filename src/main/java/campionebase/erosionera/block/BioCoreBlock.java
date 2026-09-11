package campionebase.erosionera.block;

import campionebase.erosionera.blockentity.BioCoreBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BioCoreBlock extends Block implements EntityBlock {

    public BioCoreBlock() {
        super(Properties.of()

        );
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        return new BioCoreBlockEntity(pos, blockState);
    }
}
