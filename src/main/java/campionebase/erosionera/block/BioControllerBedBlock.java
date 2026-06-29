package campionebase.erosionera.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BioControllerBedBlock extends HorizontalDirectionalBlock {
    public static final EnumProperty<BedPart> BED_PART = BlockStateProperties.BED_PART;
    public static final BooleanProperty OCCUPIED = BlockStateProperties.OCCUPIED;

    protected static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 8, 16);

    public BioControllerBedBlock() {
        super(BlockBehaviour.Properties.of()
                .sound(SoundType.METAL)
                .noOcclusion()
                .strength(1.0f)
                .pushReaction(PushReaction.IGNORE)
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(BED_PART, BedPart.FOOT).setValue(OCCUPIED, Boolean.FALSE));
    }

    private BlockPos getNeighbourPos(BlockState blockState, BlockPos pos){
        Direction facing = blockState.getValue(FACING);
        return pos.relative(blockState.getValue(BED_PART) == BedPart.FOOT ? facing : facing.getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, BED_PART, OCCUPIED);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state,
                                        @NotNull BlockGetter level,
                                        @NotNull BlockPos pos,
                                        @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // 玩家水平朝向，脚在放置位置，头在玩家朝向方向
        Direction facing = context.getHorizontalDirection();
        BlockPos blockPos = context.getClickedPos().relative(facing);
        Level level = context.getLevel();
        if (level.getBlockState(blockPos).canBeReplaced(context) && level.getWorldBorder().isWithinBounds(blockPos))
            return defaultBlockState()
                    .setValue(FACING, facing)
                    .setValue(BED_PART, BedPart.FOOT)
                    .setValue(OCCUPIED, false);
        // 不符合放置条件
        else return null;
    }

    @Override
    public void setPlacedBy(@NotNull Level level,
                            @NotNull BlockPos pos,
                            @NotNull BlockState blockState,
                            @Nullable LivingEntity placer,
                            @NotNull ItemStack itemStack) {
        super.setPlacedBy(level, pos, blockState, placer, itemStack);

        Direction facing = blockState.getValue(FACING);
        BlockPos headPos = pos.relative(facing);
        BlockState headState = blockState.setValue(BED_PART, BedPart.HEAD);
        level.setBlock(headPos, headState, Block.UPDATE_ALL);
    }

    @Override
    public void playerWillDestroy(@NotNull Level level,
                                  @NotNull BlockPos pos,
                                  @NotNull BlockState blockState,
                                  @NotNull Player player) {
        BlockPos neighbourPos = this.getNeighbourPos(blockState, pos);
        BlockState neighbourState = level.getBlockState(neighbourPos);
        if (neighbourState.is(this)){
            level.setBlock(neighbourPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_SUPPRESS_DROPS | Block.UPDATE_ALL);
        }
        super.playerWillDestroy(level, pos, blockState, player);
    }

    @Override
    public boolean isBed(BlockState state, BlockGetter level, BlockPos pos, @Nullable Entity player) {
        return true;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState blockState,
                                          @NotNull Level level,
                                          @NotNull BlockPos pos,
                                          @NotNull Player player,
                                          @NotNull InteractionHand hand,
                                          @NotNull BlockHitResult hitResult)
    {
        if (level.isClientSide()) return InteractionResult.CONSUME;
        if (blockState.getValue(OCCUPIED)) return InteractionResult.PASS;
        player.setSleepingPos(pos);
        player.setPose(Pose.SLEEPING);
        level.setBlock(pos, blockState.setValue(OCCUPIED, true), Block.UPDATE_ALL);
        return InteractionResult.PASS;
    }
}
