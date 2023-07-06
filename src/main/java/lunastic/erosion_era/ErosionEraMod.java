package lunastic.erosion_era;

import lunastic.erosion_era.init.ErErCommand;
import lunastic.erosion_era.world.biome.BiomeHelper;
import lunastic.erosion_era.world.feature.ShimmerPillarFeature;
import lunastic.erosion_era.init.*;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.TheEndBiomes;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;

public class ErosionEraMod implements ModInitializer {

	public static final String ID = "erosion_era";
	public static final String NAME = "Erosion Era";
	public static final String VERSION = "${version}";

	public static final Logger LOGGER = LogManager.getLogger(NAME);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		this.init();
		this.test();

	}

	public void init(){
		ErErGroups.init();
		ErErItems.init();
		ErErBlocks.init();
		ErErBlockItems.init();
		ErErFeatures.init();
		ErErStatusEffects.init();
		ErErRules.init();
		ErErCommand.init();
	}

	public void test(){
		// ConfiguredFeature 就是将 Feature 和 FeatureConfig 封装的类，及配置好的地物
		ConfiguredFeature<?, ?> SHIMMER_COL_FEATURE = new ConfiguredFeature<>(
				ErErFeatures.SHIMMER_PILLAR, ShimmerPillarFeature.Config.MIDDLE_INACTIVE
		);
		// 将配置好的地物 （ConfiguredFeature）应用于给定位置生成
		PlacedFeature EXAMPLE_FEATURE_PLACED = new PlacedFeature(
				RegistryEntry.of(SHIMMER_COL_FEATURE),
				// SquarePlacementModifier makes the feature generate a cluster of pillars each time
				List.of(SquarePlacementModifier.of())
		);

		Registry.register(BuiltinRegistries.PLACED_FEATURE, ErosionEraMod.identifier("shimmer_pillar"), EXAMPLE_FEATURE_PLACED);
		Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, ErosionEraMod.identifier("shimmer_pillar"), SHIMMER_COL_FEATURE);
		BiomeModifications.addFeature(
				BiomeSelectors.all(),
				GenerationStep.Feature.SURFACE_STRUCTURES,
				RegistryKey.of(Registry.PLACED_FEATURE_KEY, ErosionEraMod.identifier("shimmer_pillar"))
		);

		// biome
		Biome biome = (new Biome.Builder())
				.precipitation(Biome.Precipitation.RAIN)
				.temperature(0.8F)
				.downfall(0.4F)
				.effects(new BiomeEffects.Builder()
						.waterColor(0x3f76e4)
						.waterFogColor(0x050533)
						.fogColor(0xc0d8ff)
						.skyColor(0x77adff)
						.build()
				)
				.spawnSettings(new SpawnSettings.Builder()
						.creatureSpawnProbability(0.9F)
						.build()
				)
				.generationSettings(BiomeHelper.erosionArea()
						.build()

				)
				.build();
		BuiltinRegistries.add(BuiltinRegistries.BIOME, ErErBiomeKeys.EROSION_AREA, biome);
		TheEndBiomes.addMainIslandBiome(ErErBiomeKeys.EROSION_AREA, 1.0);
	}

	// "erosion_era:name_id"
	public static Identifier identifier(String id){
		return new Identifier(ErosionEraMod.ID, id);
	}
}