package com.lunastic.erosion_era;

import com.lunastic.erosion_era.block.ErosionEraBlocks;
import com.lunastic.erosion_era.item.ErosionEraItems;
import net.fabricmc.api.ModInitializer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ErosionEraMod implements ModInitializer {

	// 此模组命名空间
	public static final String NAMESPACE = "erosion_era";

	public static final Logger LOGGER = LogManager.getLogger(NAMESPACE);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Erosion Befalling!");

		ErosionEraItems.registering();
		ErosionEraBlocks.registering();
	}
}