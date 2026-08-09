package campionebase.erosionera.block;

import campionebase.erosionera.api.IBioConnector;
import campionebase.erosionera.api.IBioMachine;
import campionebase.erosionera.blockentity.AbstractBioConnectorBlockEntity;
import campionebase.erosionera.blockentity.BioNodeBlockEntity;
import campionebase.erosionera.network.BioMachineryService;
import campionebase.erosionera.network.BioNetData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BioNodeBlock extends Block implements EntityBlock {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public BioNodeBlock() {
        super(BlockBehaviour.Properties.of()

        );
        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    @Override
    public void onPlace(@NotNull BlockState newState,
                        @NotNull Level level,
                        @NotNull BlockPos pos,
                        @NotNull BlockState oldState,
                        boolean movedByPiston)
    {
        if (level instanceof ServerLevel serverLevel){
            this.scanAddConnectNeighbors(serverLevel, pos);
        }
        super.onPlace(newState, level, pos, oldState, movedByPiston);
    }

    @Override
    public void onRemove(@NotNull BlockState oldState,
                         @NotNull Level level,
                         @NotNull BlockPos pos,
                         @NotNull BlockState newState,
                         boolean movedByPiston)
    {
        if (level instanceof ServerLevel serverLevel) {
            BioMachineryService.removeNode(serverLevel, pos);
        }
        super.onRemove(oldState, level, pos, newState, movedByPiston);
    }

    @Override
    public void neighborChanged(@NotNull BlockState blockState,
                                @NotNull Level level,
                                @NotNull BlockPos pos,
                                @NotNull Block neighborBlock,
                                @NotNull BlockPos neighborPos,
                                boolean movedByPiston)
    {
        if (level instanceof ServerLevel serverLevel) {
            if (this.isValidBlock(serverLevel, pos, neighborPos)) {
                BioMachineryService.connectNodes(serverLevel, pos, neighborPos);
            }

            boolean flag = blockState.getValue(LIT);
            if (flag != serverLevel.hasNeighborSignal(pos)){
                if (flag) {
                    serverLevel.scheduleTick(pos, this, 1);
                } else {
                    serverLevel.setBlock(pos, blockState.setValue(LIT, true), Block.UPDATE_CLIENTS);
                    BioMachineryService.removeNode(serverLevel, pos);
                }
            }
        }
    }

    @Override
    public void tick(@NotNull BlockState blockState,
                     @NotNull ServerLevel level,
                     @NotNull BlockPos pos,
                     @NotNull RandomSource randomSource)
    {
        if (blockState.getValue(LIT) && !level.hasNeighborSignal(pos)){
            level.setBlock(pos, blockState.setValue(LIT, false), Block.UPDATE_CLIENTS);
            this.scanAddConnectNeighbors(level, pos);
        }
    }

    private void scanAddConnectNeighbors(ServerLevel level, BlockPos node){
        for (Direction dir : Direction.values()) {
            BlockPos neighbor = node.relative(dir);
            if (this.isValidBlock(level, node, neighbor)){
                BioMachineryService.connectNodes(level, node, neighbor);
            }
        }
    }

    private boolean isValidBlock(ServerLevel level, BlockPos node, BlockPos neighbor){
        if (level.getBlockEntity(neighbor) instanceof AbstractBioConnectorBlockEntity connector){
            Direction facing = connector.getBlockState().getValue(BlockStateProperties.FACING);
            if (node.relative(facing).equals(neighbor)){
                return true;
            }
        }
        return level.getBlockState(neighbor).getBlock() instanceof BioNodeBlock;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        return new BioNodeBlockEntity(pos, blockState);
    }
}
