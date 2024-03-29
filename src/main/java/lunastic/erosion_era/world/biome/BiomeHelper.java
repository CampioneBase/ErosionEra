package lunastic.erosion_era.world.biome;

import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;

/**
 * 辅助配置群系构造者
 * @see DefaultBiomeFeatures
 */
public abstract class BiomeHelper {
    private BiomeHelper(){}
    public static GenerationSettings.Builder erosionArea(){
        GenerationSettings.Builder builder = defaultBuilder();
        return builder;
    }

    public static GenerationSettings.Builder defaultBuilder(){
        GenerationSettings.Builder builder = new GenerationSettings.Builder();

        DefaultBiomeFeatures.addLandCarvers(builder);
        return builder;
    }
}
