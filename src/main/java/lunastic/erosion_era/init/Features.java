package lunastic.erosion_era.init;

import lunastic.erosion_era.ErosionEraMod;
import lunastic.erosion_era.world.feature.ShimmerPillarFeature;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;

public class Features {

    public static final Feature<ShimmerPillarFeature.Config> SHIMMER_PILLAR = register("shimmer_pillar", new ShimmerPillarFeature());

    private static <C extends FeatureConfig, F extends Feature<C>> F register(String id, F feature) {
        return  Registry.register(Registry.FEATURE, ErosionEraMod.identifier(id), feature);
    }
}
