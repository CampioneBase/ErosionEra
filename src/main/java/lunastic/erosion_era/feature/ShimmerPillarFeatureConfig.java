package lunastic.erosion_era.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

/**
 *
 * @param height 高度
 * @param core 核心方块
 * @param pillar 柱方块
 */
public record ShimmerPillarFeatureConfig(IntProvider height, BlockStateProvider core, BlockStateProvider pillar) implements FeatureConfig {
    public static final Codec<ShimmerPillarFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            IntProvider.VALUE_CODEC.fieldOf("height").forGetter(ShimmerPillarFeatureConfig::height),
            BlockStateProvider.TYPE_CODEC.fieldOf("core").forGetter(ShimmerPillarFeatureConfig::core),
            BlockStateProvider.TYPE_CODEC.fieldOf("pillar").forGetter(ShimmerPillarFeatureConfig::pillar)
    ).apply(instance, instance.stable(ShimmerPillarFeatureConfig::new)));
}
