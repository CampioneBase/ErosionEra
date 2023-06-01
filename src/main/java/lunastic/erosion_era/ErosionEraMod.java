package lunastic.erosion_era;

import lunastic.erosion_era.feature.ShimmerPillarFeatureConfig;
import lunastic.erosion_era.init.*;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;


public class ErosionEraMod implements ModInitializer {

	public static final String ID = "erosion_era";
	public static final String NAME = "Erosion Era";
	public static final String VERSION = "${version}";

	public static final Logger LOGGER = LogManager.getLogger(ID);

	// ConfiguredFeature 就是将 Feature 和 FeatureConfig 封装的类，及配置好的地物
	public static final ConfiguredFeature<?, ?> SHIMMER_COL_FEATURE = new ConfiguredFeature<>(
			ErErFeatures.SHIMMER_COL, new ShimmerPillarFeatureConfig(UniformIntProvider.create(5, 8), BlockStateProvider.of(ErErBlocks.SHIMMER_PILLAR))
	);

	// 将配置好的地物 （ConfiguredFeature）应用于给定位置生成
	public static PlacedFeature EXAMPLE_FEATURE_PLACED = new PlacedFeature(
			RegistryEntry.of(SHIMMER_COL_FEATURE
//                    the SquarePlacementModifier makes the feature generate a cluster of pillars each time
			), List.of(SquarePlacementModifier.of())
	);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		this.init();

		Registry.register(BuiltinRegistries.PLACED_FEATURE, ErosionEraMod.identifier("shimmer_pillar"), EXAMPLE_FEATURE_PLACED);
		Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, ErosionEraMod.identifier("shimmer_pillar"), SHIMMER_COL_FEATURE);
		BiomeModifications.addFeature(
				BiomeSelectors.all(),
				GenerationStep.Feature.FLUID_SPRINGS,
				RegistryKey.of(Registry.PLACED_FEATURE_KEY, ErosionEraMod.identifier("shimmer_pillar"))
		);
	}

	public void init(){
		ErErGroups.init();
		ErErItems.init();
		ErErBlocks.init();
		ErErBlockItems.init();
		ErErFeatures.init();
		ErErStatusEffects.init();
	}

	public static Identifier identifier(String id){
		return new Identifier(ErosionEraMod.ID, id);
	}
}