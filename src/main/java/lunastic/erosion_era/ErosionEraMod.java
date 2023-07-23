package lunastic.erosion_era;

import lunastic.erosion_era.util.data.DataCompound;
import lunastic.erosion_era.data.ErosionData;
import lunastic.erosion_era.init.Commands;
import lunastic.erosion_era.util.data.PlayerExtraData;
import lunastic.erosion_era.world.feature.ShimmerPillarFeature;
import lunastic.erosion_era.init.*;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class ErosionEraMod implements ModInitializer {

	public static final String ID = "erosion_era";
	public static final String NAME = "Erosion Era";
	public static final String VERSION = "${version}";

	public static final Logger LOGGER = LogManager.getLogger(NAME);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// 一旦Minecraft处于mod加载就绪状态，就会运行此代码。
		// However, some things (like resources) may still be uninitialized.
		// 然而，有些东西（如资源）可能仍然没有初始化。
		// Proceed with mild caution.
		// 谨慎行事。
		this.init();
		this.test();
		this.data();
	}

	private void data() {
		DataCompound.register(ErosionData::create);
	}

	public void init(){
		ItemGroups.init();
		Items.init();
		Blocks.init();
		BlockItems.init();
		StatusEffects.init();
		Rules.init();
		Commands.init();
		PlacedFeatures.init();
	}

	public void test(){
//		// ConfiguredFeature 就是将 Feature 和 FeatureConfig 封装的类，及配置好的地物
//		ConfiguredFeature<?, ?> SHIMMER_COL_FEATURE = new ConfiguredFeature<>(
//				Features.SHIMMER_PILLAR, ShimmerPillarFeature.Config.MIDDLE_INACTIVE
//		);
//		// 将配置好的地物 （ConfiguredFeature）应用于给定位置生成
//		PlacedFeature EXAMPLE_FEATURE_PLACED = new PlacedFeature(
//				RegistryEntry.of(SHIMMER_COL_FEATURE),
//				// SquarePlacementModifier makes the feature generate a cluster of pillars each time
//				List.of(SquarePlacementModifier.of())
//		);
//
//		Registry.register(BuiltinRegistries.PLACED_FEATURE, ErosionEraMod.identifier("shimmer_pillar"), EXAMPLE_FEATURE_PLACED);
//		Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, ErosionEraMod.identifier("shimmer_pillar"), SHIMMER_COL_FEATURE);
//		BiomeModifications.addFeature(
//				BiomeSelectors.all(),
//				GenerationStep.Feature.SURFACE_STRUCTURES,
//				RegistryKey.of(Registry.PLACED_FEATURE_KEY, ErosionEraMod.identifier("shimmer_pillar"))
//		);
	}

	// "erosion_era:name_id"
	public static Identifier identifier(String id){
		return new Identifier(ErosionEraMod.ID, id);
	}
}