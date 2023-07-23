package lunastic.erosion_era.world.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import lunastic.erosion_era.init.Blocks;
import lunastic.erosion_era.init.Tags;
import net.minecraft.block.Block;
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
    /** 非活性状态代码 */
    public static final int INACTIVE_STATUE = 0;
    /** 活性状态代码 */
    public static final int ACTIVE_STATUE = 1;

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
        return bs.isIn(Tags.EROSION_ENV);
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
        BlockState coreBlockState ,pillarBlockState;
        switch (config.isActive().get(random)) {
            // 非活性
            case INACTIVE_STATUE -> {
                coreBlockState = Blocks.SHIMMER_CORE.getDefaultState();
                pillarBlockState = Blocks.SHIMMER_PILLAR.getDefaultState();
            }
            // 活性
            case ACTIVE_STATUE -> {
                // todo 活性状态设置方块
                coreBlockState = Blocks.SHIMMER_CORE.getDefaultState();
                pillarBlockState = Blocks.SHIMMER_PILLAR.getDefaultState();
            }
            default -> {
                return false;
            }
        }

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
                // -- setBlockState 注释 -- //
                // 生成方块状态的 flag 意义
                // 1：NOTIFY_NEIGHBORS   向周围的块发送邻居更新事件。
                // 2：NOTIFY_LISTENERS   通知需要在块更改时做出反应的侦听器和客户端。
                // 4：NO_REDRAW          与NOTIFY_LISTENERS一起使用，以抑制客户端上的渲染过程。
                // 8：REDRAW_ON_MAIN_THREAD 强制客户端进行同步重绘。
                // 16：FORCE_STATE       绕过虚拟块状态更改并强制将传递的状态按原样存储。
                // 32：SKIP_DROPS        防止前一个方块（容器）在销毁时丢弃物品。
                // 64：MOVED             表示当前块正被移动到不同位置的信号，通常是因为活塞。
                // 128：SKIP_LIGHTING_UPDATES 应跳过照明更新的信号。

                // 3:NOTIFY_ALL NOTIFY_NEIGHBORS + NOTIFY_LISTENERS

                // 中心柱
                this.createSimplePillar(world, random, pillarBlockState, corePos, height, 4);
                BlockPos finalCorePos = corePos;
                // 四周方块生成
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
                        this.createSimplePillar(world, random, pillarBlockState, p, h, d);
                        // 绕Y顺时针
                        this.createSimplePillar(world, random, pillarBlockState,
                                p.offset(direction.rotateYClockwise()),
                                random.nextBetween(h / 2, h * 3 / 4), d);
                        // 绕Y逆时针
                        this.createSimplePillar(world, random, pillarBlockState,
                                p.offset(direction.rotateYCounterclockwise()),
                                random.nextBetween(h / 2, h * 3 / 4), d);
                        // 深度递减
                        d = Math.max(d - 1, 1);
                    } while (true);
                });
                // 核心方块 生成时向周围发送更新
                world.setBlockState(corePos, coreBlockState, Block.NOTIFY_ALL);
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
    private BlockPos createSimplePillar(StructureWorldAccess world, Random random, BlockState state, BlockPos pos, int height, int depth){
        // 高度 深度 计数 偏移触发量
        int h = 0, d = 0, c = 0, s = this.slope;
        // 指针点
        BlockPos p = pos, result = null;
        // 记录一个中心方块和最深方块
        BlockState b1 = world.getBlockState(pos), b2 = world.getBlockState(pos.down(depth));

        while (d++ < depth){
            world.setBlockState(p, state, Block.NOTIFY_LISTENERS);
            p = p.down();
        }
        // 回归中心
        p = pos;
        while (h++ < height){
            world.setBlockState(p, state, Block.NOTIFY_LISTENERS);
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
        else if(world.getBlockState(result).isIn(Tags.EROSION_ENV)) return p;
        // 判断是否可以生成拐点方块
        if(random.nextBetween(0,5) > 1 && this.ignoredBlocks(b1)){
            if(random.nextBetween(0,8) > 1 && this.ignoredBlocks(b2)) {
                // 生成 2 个
                world.setBlockState(result.up(), b1, Block.NOTIFY_LISTENERS);
                world.setBlockState(result, b2, Block.NOTIFY_LISTENERS);
            } else {
                // 生成 1 个
                world.setBlockState(result, b1, Block.NOTIFY_LISTENERS);
            }
        }
        return p;
    }

    // 默认配置
    public static record Config(
            IntProvider height,
            IntProvider slope,
            IntProvider isActive
    ) implements FeatureConfig {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                IntProvider.VALUE_CODEC.fieldOf("height").forGetter(Config::height),
                IntProvider.VALUE_CODEC.fieldOf("slope").forGetter(Config::slope),
                IntProvider.VALUE_CODEC.fieldOf("isActive").forGetter(Config::isActive)
        ).apply(instance, instance.stable(Config::new)));

        public static final Config MIDDLE_INACTIVE = new Config(
                UniformIntProvider.create(16, 24),
                ConstantIntProvider.create(8),
                ConstantIntProvider.create(INACTIVE_STATUE)
        );

        public static final Config LARGE_INACTIVE = new Config(
                UniformIntProvider.create(32, 60),
                ConstantIntProvider.create(12),
                ConstantIntProvider.create(INACTIVE_STATUE)
        );
    }
}
