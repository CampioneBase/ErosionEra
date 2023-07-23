package lunastic.erosion_era.init;

import lunastic.erosion_era.ErosionEraMod;
import lunastic.erosion_era.init.Features;
import lunastic.erosion_era.world.feature.ShimmerPillarFeature;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;

public abstract class ConfiguredFeatures {
    private ConfiguredFeatures(){throw new RuntimeException();}

    public static final ConfiguredFeature<?, ?> SHIMMER_PILLAR_NORMAL = register("shimmer_pillar_normal",
            Features.SHIMMER_PILLAR, ShimmerPillarFeature.Config.MIDDLE_INACTIVE);
    public static final ConfiguredFeature<?, ?> SHIMMER_PILLAR_LARGE = register("shimmer_pillar_large",
            Features.SHIMMER_PILLAR, ShimmerPillarFeature.Config.LARGE_INACTIVE);

    // ConfiguredFeature 就是将 Feature 和 FeatureConfig 封装的类，及配置好的地物
    private static <FC extends FeatureConfig, F extends Feature<FC>> ConfiguredFeature<FC, F> register(
            String id, F feature, FC featureConfig
    ){
        return Registry.register(
                BuiltinRegistries.CONFIGURED_FEATURE,
                ErosionEraMod.identifier(id),
                new ConfiguredFeature<>(feature, featureConfig)
        );
    }
}
