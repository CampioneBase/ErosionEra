package lunastic.erosion_era;

import lunastic.erosion_era.feature.ShimmerFeature;
import net.fabricmc.api.ModInitializer;

import net.minecraft.item.ItemGroup;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import lunastic.erosion_era.item.items.*;


public class ErosionEraMod implements ModInitializer {

	public static final String ID = "erosion_era";
	public static final String NAME = "Erosion Era";
	public static final String VERSION = "${version}";

	public static final Logger LOGGER = LogManager.getLogger(ID);

	private static final Feature<DefaultFeatureConfig> SHIMMER = new ShimmerFeature(DefaultFeatureConfig.CODEC);

	public static final ConfiguredFeature<?,?> CONFIGURED_FEATURE = SHIMMER
			.configure(new DefaultFeatureConfig())
			.spreadHorizontally()
			.applyChance(6);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		for(ItemGroup group : new ItemGroup[]{
				ArmorItems.GROUP,
				BasicItems.GROUP,
				ErodedBlockItems.GROUP,
				ToolItems.GROUP,

				EnvBlockItems.GROUP,
				ErodedBlockItems.GROUP
		}) LOGGER.info("Loading " + group.getName());

//		RegistryKey<ConfiguredFeature<?, ?>> shimmer_col = RegistryKey.of(Registry.CONFIGURED_FEATURE_WORLDGEN,
//				new Identifier(NAMESPACE, "shimmer_col"));
//		Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, shimmer_col.getValue(), CONFIGURED_FEATURE);
//		BiomeModifications.addFeature(BiomeSelectors.all(), GenerationStep.Feature.UNDERGROUND_ORES, shimmer_col);
	}
}