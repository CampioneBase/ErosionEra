package com.lunastic.erosion_era.block.eroded;

import com.lunastic.erosion_era.block.ErosionEraBlocks;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.light.ChunkLightProvider;

import java.util.Random;

/**
 * <H2>受侵蚀草方块</H2>
 * 和原版草方块相比：
 * <ul>
 *     <li>会因为积雪改变贴图</li>
 *     <li>不会扩张</li>
 *     <li>会退化成受侵蚀泥土</li>
 *     <li>无法种植（骨粉）</li>
 *     <li>无法开垦</li>
 * </ul>
 * <p>
 *     此外，我发现能否种花是是写死在种植方块类{@link PlantBlock}里的（即，判断右击的方块是否为特定方块）
 * </p>
 * @author Lunastic
 */
public class ErodedGrassBlock extends ErodedBlock {
    public static final BooleanProperty SNOWY = Properties.SNOWY;

    public ErodedGrassBlock() {
        super(ErodedGrassBlock.Settings
                .copyOf(Blocks.GRASS_BLOCK)
        );
        // 添加默认积雪状态
        this.setDefaultState((this.stateManager.getDefaultState()).with(SNOWY, false));
        // 使用草方块的纹理，添加侵蚀颜色
        ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> 8368696, this);
        ColorProviderRegistry.ITEM.register((itemStack, layer) -> 8368696, this);
    }

    // -- Copy from SnowyBlock.java
    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        if (direction == Direction.UP) {
            return state.with(SNOWY, newState.isOf(Blocks.SNOW_BLOCK) || newState.isOf(Blocks.SNOW));
        }
        return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos().up());
        return this.getDefaultState().with(SNOWY, blockState.isOf(Blocks.SNOW_BLOCK) || blockState.isOf(Blocks.SNOW));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(SNOWY);
    }
    // -- end copy

    // -- Copy from SpreadableBlock.java
    private static boolean canSurvive(BlockState state, WorldView worldView, BlockPos pos) {
        BlockPos blockPos = pos.up(); // 上方方块位置
        BlockState blockState = worldView.getBlockState(blockPos);
        // 上方方块有积雪且层数为1
        if (blockState.isOf(Blocks.SNOW) && blockState.get(SnowBlock.LAYERS) == 1) {
            return true;
        }
        if (blockState.getFluidState().getLevel() == 8) {
            return false;
        }
        int i = ChunkLightProvider.getRealisticOpacity(worldView, state, pos, blockState, blockPos, Direction.UP, blockState.getOpacity(worldView, blockPos));
        return i < worldView.getMaxLightLevel();
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!ErodedGrassBlock.canSurvive(state, world, pos)) {
            world.setBlockState(pos, ErosionEraBlocks.ERODED_DIRT.getDefaultState());
        }
    }
    // -- end copy
}

