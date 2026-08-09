package campionebase.erosionera.block;

import campionebase.erosionera.api.IBioObservable;
import campionebase.erosionera.blockentity.BioRedstoneBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BioRedstoneBlock extends Block implements EntityBlock, IBioObservable.BlockSource {
    public static final IntegerProperty POWER = BlockStateProperties.POWER;

    public BioRedstoneBlock() {
        super(BlockBehaviour.Properties.of()

        );
        this.registerDefaultState(this.stateDefinition.any().setValue(POWER, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWER);
    }

    @Override
    public int getSignal(@NotNull BlockState blockState,
                         @NotNull BlockGetter level,
                         @NotNull BlockPos pos,
                         @NotNull Direction direction)
    {
        return blockState.getValue(POWER);
    }

    @Override
    public boolean isSignalSource(@NotNull BlockState blockState) {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        return new BioRedstoneBlockEntity(pos, blockState);
    }

    @Override
    public @NotNull List<Component> getInfo(BlockState source) {
        if (!source.hasProperty(POWER)) return List.of();
        return List.of(Component.literal("Power: " + source.getValue(POWER)));
    }
}
