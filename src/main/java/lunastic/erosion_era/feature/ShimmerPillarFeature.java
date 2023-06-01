package lunastic.erosion_era.feature;

import com.mojang.serialization.Codec;
import lunastic.erosion_era.ErosionEraMod;

import lunastic.erosion_era.block.environment.EnvBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;
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
        // the origin is the place where the game starts trying to place the feature
        BlockPos origin = context.getOrigin();
        // we won't use the random here, but we could if we wanted to
        Random random = context.getRandom();
        ShimmerPillarFeatureConfig config = context.getConfig();

        // don't worry about where these come from-- we'll implement these methods soon
        int height = config.height().get(random);

        BlockState blockState = Registry.BLOCK.get(ErosionEraMod.identifier("shimmer_pillar")).getDefaultState();
        blockState.with(EnvBlock.ORIGINAL, true);

        // find the surface of the world
        BlockPos testPos = new BlockPos(origin);
        for (int y = 0; y < world.getHeight(); y++) {
            testPos = testPos.up();
            // the tag name is dirt, but includes grass, mud, podzol, etc.
            if (world.getBlockState(testPos).isIn(BlockTags.DIRT)) {
                if (world.getBlockState(testPos.up()).isOf(Blocks.AIR)) {
                    for (int i = 0; i < height; i++) {
//            create a simple pillar of blocks
                        world.setBlockState(testPos, blockState, 0x10);
                        testPos = testPos.up();

                        // ensure we don't try to place blocks outside the world
                        if (testPos.getY() >= world.getTopY()) break;
                    }
                    return true;
                }
            }
        }
        return false;
    }

}
