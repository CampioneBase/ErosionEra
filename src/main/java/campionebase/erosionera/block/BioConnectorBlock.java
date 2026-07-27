package campionebase.erosionera.block;

import campionebase.erosionera.block.entity.AbstractBioConnectorBlockEntity;
import campionebase.erosionera.block.entity.BioConnectorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class BioConnectorBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final VoxelShape SHAPE_UP = Shapes.or(
            Block.box(5, 0, 5, 11, 1,  11),
            Block.box(6, 1, 6, 10, 8, 10)
    );
    public static final VoxelShape SHAPE_DOWN = Shapes.or(
            Block.box(5, 15, 5, 11, 16, 11),
            Block.box(6, 8, 6, 10, 15, 10)
    );

    public static final VoxelShape SHAPE_NORTH = Shapes.or(
            Block.box(5, 5, 15, 11, 11, 16),
            Block.box(6, 6, 8, 10, 10, 15)
    );

    public static final VoxelShape SHAPE_SOUTH = Shapes.or(
            Block.box(5, 5, 0, 11, 11, 1),
            Block.box(6, 6, 1, 10, 10, 8)
    );

    public static final VoxelShape SHAPE_WEST = Shapes.or(
            Block.box(15, 5, 5, 16, 11, 11),
            Block.box(8, 6, 6, 15, 10, 10)
    );

    public static final VoxelShape SHAPE_EAST = Shapes.or(
            Block.box(0, 5, 5, 1, 11, 11),
            Block.box(1, 6, 6, 8, 10, 10)
    );

    public BioConnectorBlock() {
        super(BlockBehaviour.Properties.of()

        );
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clickedFace = context.getClickedFace();

        Level level = context.getLevel();
        BlockPos targetPos = context.getClickedPos().relative(clickedFace.getOpposite());
        BlockState targetState = level.getBlockState(targetPos);
        // 正常放置逻辑
        if (targetState.isFaceSturdy(level, targetPos, clickedFace, SupportType.CENTER)){
            return this.defaultBlockState().setValue(FACING, clickedFace);
        }

        // 如果是可替换方块（草），则检测其下方是否可以放置
        if (context.canPlace()){
            BlockPos bottomPos = context.getClickedPos().below();
            BlockState bottomState = level.getBlockState(bottomPos);
            if (bottomState.isFaceSturdy(level, bottomPos, Direction.UP, SupportType.CENTER)){
                return this.defaultBlockState().setValue(FACING, Direction.UP);
            }
        }
        return null;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState blockState,
                                        @NotNull BlockGetter level,
                                        @NotNull BlockPos pos,
                                        @NotNull CollisionContext context) {
        return switch (blockState.getValue(FACING)){
            default -> SHAPE_UP;
            case DOWN -> SHAPE_DOWN;
            case NORTH -> SHAPE_NORTH;
            case SOUTH -> SHAPE_SOUTH;
            case EAST -> SHAPE_EAST;
            case WEST -> SHAPE_WEST;
        };
    }

    public @NotNull BlockState rotate(BlockState blockState, Rotation rotation) {
        return blockState.setValue(FACING, rotation.rotate(blockState.getValue(FACING)));
    }

    public @NotNull BlockState mirror(BlockState blockState, Mirror mirror) {
        return blockState.rotate(mirror.getRotation(blockState.getValue(FACING)));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        return new BioConnectorBlockEntity(pos, blockState);
    }

    @Override
    public void neighborChanged(@NotNull BlockState blockState,
                                @NotNull Level level,
                                @NotNull BlockPos pos,
                                @NotNull Block neighborBlock,
                                @NotNull BlockPos neighborPos,
                                boolean movedByPiston)
    {
        Direction facing = blockState.getValue(FACING);
        BlockPos oppositePos = pos.relative(facing.getOpposite());
        if (!level.getBlockState(oppositePos).isFaceSturdy(level, neighborPos, facing, SupportType.CENTER)) {
            if (level.getBlockEntity(pos) instanceof AbstractBioConnectorBlockEntity connector) {
                connector.removeNode();
            }
            level.destroyBlock(pos, true);
        }
        super.neighborChanged(blockState, level, pos, neighborBlock, neighborPos, movedByPiston);
    }
}
