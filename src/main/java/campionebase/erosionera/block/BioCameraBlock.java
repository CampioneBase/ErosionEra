package campionebase.erosionera.block;

import campionebase.erosionera.blockentity.AbstractBioConnectorBlockEntity;
import campionebase.erosionera.blockentity.BioCameraBlockEntity;
import campionebase.erosionera.client.screen.BioCameraNamingScreen;
import campionebase.erosionera.network.BioMachineryService;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BioCameraBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.VERTICAL);
    public static final VoxelShape SHAPE_UP = Shapes.or(
            Block.box(0, 0, 0, 16, 4,16),
            Block.box(5, 4, 5, 11, 10, 11)
    );

    public static final VoxelShape SHAPE_DOWN = Shapes.or(
            Block.box(0, 12, 0, 16, 16,16),
            Block.box(5, 6, 5, 11, 12, 11)
    );

    public BioCameraBlock() {
        super(BlockBehaviour.Properties.of()

        );
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
    }

    public @NotNull BlockState rotate(BlockState blockState, Rotation rotation) {
        return blockState.setValue(FACING, rotation.rotate(blockState.getValue(FACING)));
    }

    public @NotNull BlockState mirror(BlockState blockState, Mirror mirror) {
        return blockState.rotate(mirror.getRotation(blockState.getValue(FACING)));
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState blockState,
                                        @NotNull BlockGetter level,
                                        @NotNull BlockPos pos,
                                        @NotNull CollisionContext context) {
        return blockState.getValue(FACING) == Direction.UP ? SHAPE_UP : SHAPE_DOWN;
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
        // 如果是可替换方块（草），则检测其下方是否可以放置
        if (context.canPlace()){
            BlockPos bottomPos = context.getClickedPos().below();
            BlockState bottomState = level.getBlockState(bottomPos);
            if (bottomState.isFaceSturdy(level, bottomPos, Direction.UP, SupportType.CENTER)){
                return this.defaultBlockState().setValue(FACING, Direction.UP);
            }
        }
        // 是否为 Y 轴方向
        if (!FACING.getPossibleValues().contains(clickedFace)) return null;

        // 正常放置逻辑
        BlockPos targetPos = context.getClickedPos().relative(clickedFace.getOpposite());
        BlockState targetState = level.getBlockState(targetPos);
        if (targetState.isFaceSturdy(level, targetPos, clickedFace, SupportType.CENTER)){
            return this.defaultBlockState().setValue(FACING, clickedFace);
        }
        return null;
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
            level.destroyBlock(pos, true);
            if (level.getBlockEntity(pos) instanceof AbstractBioConnectorBlockEntity connector) {
                if (level instanceof ServerLevel serverLevel) {
                    BioMachineryService.removedNode(serverLevel, connector);
                }
            }
        }
        super.neighborChanged(blockState, level, pos, neighborBlock, neighborPos, movedByPiston);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state,
                                          @NotNull Level level,
                                          @NotNull BlockPos pos,
                                          @NotNull Player player,
                                          @NotNull InteractionHand hand,
                                          @NotNull BlockHitResult hit)
    {
        ItemStack itemStack = player.getItemInHand(hand);
        if (level.isClientSide && itemStack.isEmpty()) {
            // 获取当前名称
            if (level.getBlockEntity(pos) instanceof BioCameraBlockEntity camera){
                Minecraft.getInstance().setScreen(new BioCameraNamingScreen(camera));
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        return new BioCameraBlockEntity(pos, blockState);
    }


}
