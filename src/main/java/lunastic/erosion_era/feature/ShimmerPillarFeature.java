package lunastic.erosion_era.feature;

import com.mojang.serialization.Codec;
import lunastic.erosion_era.ErosionEraMod;

import lunastic.erosion_era.block.environment.EnvBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class ShimmerPillarFeature extends Feature<ShimmerPillarFeatureConfig> {
    public ShimmerPillarFeature(Codec<ShimmerPillarFeatureConfig> configCodec) {
        super(configCodec);
    }

    // 区块决定生成地物时会调用 generate。
    // 如果地物配置为在每个区块生成，则每个区块被生成时都会调用一次。
    // 如果地物被配置为在每个生物群系以特定的概率生成，generate 只会在世界需要生成结构的实例中调用。
    @Override
    public boolean generate(FeatureContext<ShimmerPillarFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        // 起始坐标
        BlockPos origin = context.getOrigin();

        Random random = context.getRandom();
        // 读取配置
        ShimmerPillarFeatureConfig config = context.getConfig();

        // 中心高度
        int height = config.height().get(random);

        // 读取核心方块
        BlockState coreBlock = config.core().getBlockState(random, origin)
                .with(EnvBlock.ORIGINAL, true);

        BlockState pillarBlock = config.pillar().getBlockState(random, origin)
                .with(EnvBlock.ORIGINAL, true);


        // 从 y = 63 层开始寻找地面
        BlockPos corePos = new BlockPos(origin.getX(), 63, origin.getZ());
        for (;
             // 避免出界
             corePos.getY() < world.getTopY() - height - 1;
             // 向上逐步
             corePos = corePos.up()
        ) {
            // 判断该店是否为空气
            if (world.getBlockState(corePos).isAir()) {
                // 定位下面的位置
                corePos = corePos.down();
                // 如果是特定方块或者标签，则继续下潜
                while (
                        // tips: 此判断可能与其他 mod 不兼容，导致会定位在奇怪的地方
                        world.getBlockState(corePos).isOf(Blocks.WATER) ||      // 水方块
                        world.getBlockState(corePos).isOf(Blocks.LAVA) ||       // 岩浆方块
                        world.getBlockState(corePos).isIn(BlockTags.ICE) ||     // 冰标签
                        world.getBlockState(corePos.down()).isAir()             // 空气类
                ) corePos = corePos.down();
                ErosionEraMod.LOGGER.info(corePos.toShortString());
                // 生成地物
                BlockPos topPos = this.createPillar(world, pillarBlock, corePos, height);
                world.setBlockState(corePos, coreBlock, 0x10);
                world.setBlockState(topPos, Blocks.GLOWSTONE.getDefaultState(), 0x10);
                // 结束生成
                break;
            }

        }


        return false;
    }


    /**
     * 生成 1x1 指定高度的柱子
     * @param world 世界
     * @param state 方块状态
     * @param pos 柱子起点坐标
     * @param height 柱子高度
     * @return 终点坐标
     */
    private BlockPos createPillar(StructureWorldAccess world, BlockState state, BlockPos pos, int height){
        for (int i = 0; i < height; i++, pos = pos.up()) {
            world.setBlockState(pos, state, 0x10);
        }
        return pos;
    }
}
