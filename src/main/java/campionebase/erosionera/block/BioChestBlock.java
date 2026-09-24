package campionebase.erosionera.block;

import campionebase.erosionera.blockentity.AbstractBioStorageBlockEntity;
import campionebase.erosionera.blockentity.BioChestBlockEntity;
import campionebase.erosionera.registry.ErErBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BioChestBlock extends BarrelBlock implements EntityBlock {

    public BioChestBlock() {
        super(BlockBehaviour.Properties.of()

        );
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState blockState,
                                          @NotNull Level level,
                                          @NotNull BlockPos pos,
                                          @NotNull Player player,
                                          @NotNull InteractionHand hand,
                                          @NotNull BlockHitResult result)
    {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (level.getBlockEntity(pos) instanceof BioChestBlockEntity chest){
            NetworkHooks.openScreen((ServerPlayer) player, chest, pos);
            player.awardStat(Stats.OPEN_BARREL);
        }

        return InteractionResult.CONSUME;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        return new BioChestBlockEntity(pos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level,
                                                                  @NotNull BlockState blockState,
                                                                  @NotNull BlockEntityType<T> type) {
        return type == ErErBlockEntities.BIO_CHEST.get() ? AbstractBioStorageBlockEntity::serverTick : null;
    }
}
