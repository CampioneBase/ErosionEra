package campionebase.erosionera.block;

import campionebase.erosionera.blockentity.AbstractBioStorageBlockEntity;
import campionebase.erosionera.blockentity.BioBinBlockEntity;
import campionebase.erosionera.registry.ErErBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BioBinBlock extends Block implements EntityBlock {
    public BioBinBlock() {
        super(BlockBehaviour.Properties.of()

        );
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        return new BioBinBlockEntity(pos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level,
                                                                  @NotNull BlockState blockState,
                                                                  @NotNull BlockEntityType<T> type) {
        return type == ErErBlockEntities.BIO_BIN.get()
                ? AbstractBioStorageBlockEntity::serverTick
                : null;
    }
}
