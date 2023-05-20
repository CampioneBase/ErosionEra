package com.lunastic.erosion_era;

import com.lunastic.erosion_era.block.ErosionEraBlocks;
import com.lunastic.erosion_era.item.items.*;
import net.fabricmc.api.ModInitializer;

import net.minecraft.item.ItemGroup;
import org.apache.commons.lang3.ArrayUtils;
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

		ErosionEraItems.registering();
		ErosionEraBlocks.registering();

		for(ItemGroup group : new ItemGroup[]{
				ArmorItems.GROUP,
				BasicItems.GROUP,
				ErodedBlockItems.GROUP,
				ToolItems.GROUP
		}){
			LOGGER.info("Loading " + group.getColumn() + " item(s)");
		}
	}
}