package lunastic.erosion_era.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public record ShimmerPillarFeatureConfig(IntProvider height, BlockStateProvider block) implements FeatureConfig {
    public static final Codec<ShimmerPillarFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            IntProvider.VALUE_CODEC.fieldOf("height").forGetter(ShimmerPillarFeatureConfig::height),
            BlockStateProvider.TYPE_CODEC.fieldOf("block").forGetter(ShimmerPillarFeatureConfig::block)
    ).apply(instance, instance.stable(ShimmerPillarFeatureConfig::new)));
}
