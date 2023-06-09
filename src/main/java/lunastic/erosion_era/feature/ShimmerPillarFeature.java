package lunastic.erosion_era.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import lunastic.erosion_era.block.environment.EnvBlock;
import lunastic.erosion_era.init.ErErBlocks;
import lunastic.erosion_era.init.ErErTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

import java.util.function.Predicate;

public class ShimmerPillarFeature extends Feature<ShimmerPillarFeature.Config> {
    public ShimmerPillarFeature() {
        super(Config.CODEC);
    }
    /** 额外判断核心位置是否需要下潜，用于外部重写扩展判断 */
    public static Predicate<FeatureContext<Config>> posCanDive = context -> false;
    /** 额外判断核心位置是否需要下潜，用于继承类重写扩展判断 */
    protected boolean ignoredBlocks(FeatureContext<Config> context, BlockPos pos){
        StructureWorldAccess world = context.getWorld();
        BlockState bs = world.getBlockState(pos);
        // 考虑到兼容问题，留出了可扩展的部分
        return posCanDive.test(context) || this.ignoredBlocks(bs);

    }

    protected boolean ignoredBlocks(BlockState bs){
        // 空气方块
        return bs.isAir() ||
                // 流动方块方块
                bs.getBlock() instanceof FluidBlock ||
                // 冰类标签
                bs.isIn(BlockTags.ICE) ||
                // 原木类标签
                bs.isIn(BlockTags.LOGS) ||
                // 树叶 类标签
                bs.isIn(BlockTags.LEAVES) ||
                // 侵蚀方块类
                this.isErosionBlock(bs);
    }

    protected boolean isErosionBlock(BlockState bs){
        return bs.isIn(ErErTags.EROSION_ENV);
    }

    // 斜度 即每上升多少方块向水平方向平移一格
    private int slope;

    // 偏移方向
    private Direction direction;

    // 区块决定生成地物时会调用 generate。
    // 如果地物配置为在每个区块生成，则每个区块被生成时都会调用一次。
    // 如果地物被配置为在每个生物群系以特定的概率生成，generate 只会在世界需要生成结构的实例中调用。
    @Override
    public boolean generate(FeatureContext<Config> context) {
        Random random = context.getRandom();
        // 2% 概率生成
        if (random.nextBetween(1, 50) > 1) return false;

        StructureWorldAccess world = context.getWorld();

        // 起始坐标
        BlockPos origin = context.getOrigin();

        // 读取配置
        Config config = context.getConfig();

        // 中心高度
        int height = config.height().get(random);

        // 斜度
        this.slope = config.slope().get(random);

        // 偏移方向
        this.direction = Direction.Type.HORIZONTAL.random(random);

        // 读取方块
        BlockState coreBlockState = config.core().getBlockState(random, origin)
                .with(EnvBlock.ORIGINAL, true);
        BlockState pillarBlockState = config.pillar().getBlockState(random, origin)
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
                // 如果是特定方块或者标签，则继续下潜 (最低为 -60 避免末地维度卡死)
                while (this.ignoredBlocks(context, corePos)) {
                    if (corePos.getY() < -60) return false;
                    corePos = corePos.down();
                }

                // ------------ 生成地物 ------------ //
                // 中心柱
                this.createLinePillar(world, random, pillarBlockState, corePos, height, 4);
                // 核心方块
                world.setBlockState(corePos, coreBlockState, 0x10);
                BlockPos finalCorePos = corePos;
                Direction.Type.HORIZONTAL.stream().iterator().forEachRemaining(direction -> {
                    int h = height, d = 4;
                    BlockPos p = finalCorePos;
                    do {
                        // 坐标向前一部
                        p = p.offset(direction);

                        // 每次高度削减 slope 的 50% ~ 100%
                        int cut1 = random.nextBetween(this.slope / 2, this.slope);
                        if(cut1 >= h) break;
                        h -= cut1;
                        this.createLinePillar(world, random, pillarBlockState, p, h, d);
                        // 右转
                        this.createLinePillar(world, random, pillarBlockState,
                                p.offset(direction.rotateYClockwise()),
                                random.nextBetween(h / 2, h * 3 / 4), d);
                        // 左转
                        this.createLinePillar(world, random, pillarBlockState,
                                p.offset(direction.rotateYCounterclockwise()),
                                random.nextBetween(h / 2, h * 3 / 4), d);
                        // 深度递减
                        d = Math.max(d - 1, 1);
                    } while (true);

                });
                // ------------ 结束生成 ------------ //
                return true;
            }

        }

        return false;
    }

    /**
     * 生成 1x1 指定高度的柱子(可倾斜)
     * @param world 世界
     * @param random 随机模式
     * @param state 方块状态
     * @param pos 柱子起点坐标
     * @param height 柱子高度
     * @param depth 深度
     * @return 终点坐标
     */
    private BlockPos createLinePillar(StructureWorldAccess world, Random random, BlockState state, BlockPos pos, int height, int depth){
        // 高度 深度 计数 偏移触发量
        int h = 0, d = 0, c = 0, s = this.slope;
        // 指针点
        BlockPos p = pos, result = null;
        // 记录一个中心方块和最深方块
        BlockState b1 = world.getBlockState(pos), b2 = world.getBlockState(pos.down(depth));

        while (d++ < depth){
            world.setBlockState(p, state, 0x10);
            p = p.down();
        }
        // 回归中心
        p = pos;
        while (h++ < height){
            world.setBlockState(p, state, 0x10);
            // 自增高
            p = p.up();
            // 计数偏移
            if(this.direction != null && this.slope != 0 && ++c % s == 0) {
                // 记录第一个拐点
                if (result == null) result = p;
                c = 0; s--; p = p.offset(this.direction);
            }
        }
        if(result == null) result = p;
        // 如果拐点是 环境方块则说明是在内侧，跳过生成
        else if(world.getBlockState(result).isIn(ErErTags.EROSION_ENV)) return p;
        // 判断是否可以生成拐点方块
        if(random.nextBetween(0,5) > 1 && this.ignoredBlocks(b1)){
            if(random.nextBetween(0,8) > 1 && this.ignoredBlocks(b2)) {
                // 生成 2 个
                world.setBlockState(result.up(), b1, 0x10);
                world.setBlockState(result, b2, 0x10);
            } else {
                // 生成 1 个
                world.setBlockState(result, b1, 0x10);
            }
        }
        return p;
    }

    // 默认配置
    public static record Config(
            IntProvider height,
            IntProvider slope,
            BlockStateProvider core,
            BlockStateProvider pillar
    ) implements FeatureConfig {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                IntProvider.VALUE_CODEC.fieldOf("height").forGetter(Config::height),
                IntProvider.VALUE_CODEC.fieldOf("slope").forGetter(Config::slope),
                BlockStateProvider.TYPE_CODEC.fieldOf("core").forGetter(Config::core),
                BlockStateProvider.TYPE_CODEC.fieldOf("pillar").forGetter(Config::pillar)

        ).apply(instance, instance.stable(Config::new)));

        public static final Config MIDDLE_INACTIVE = new Config(
                UniformIntProvider.create(16, 24),
                ConstantIntProvider.create(8),
                BlockStateProvider.of(ErErBlocks.SHIMMER_CORE),
                BlockStateProvider.of(ErErBlocks.SHIMMER_PILLAR)
        );
    }
}
