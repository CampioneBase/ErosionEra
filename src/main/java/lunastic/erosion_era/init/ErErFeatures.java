package lunastic.erosion_era.init;

import lunastic.erosion_era.ErosionEraMod;
import lunastic.erosion_era.feature.ShimmerPillarFeature;
import lunastic.erosion_era.feature.ShimmerPillarFeatureConfig;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;

public class ErErFeatures {

    public static final Feature<ShimmerPillarFeatureConfig> SHIMMER_COL = register("shimmer_col", new ShimmerPillarFeature(ShimmerPillarFeatureConfig.CODEC));

    private static <C extends FeatureConfig, F extends Feature<C>> F register(String id, F feature) {
        return (F) Registry.register(Registry.FEATURE, ErosionEraMod.identifier(id), feature);
    }

    public static void init(){
        ErosionEraMod.LOGGER.info("Features Loading......");
    }
}
