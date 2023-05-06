package com.lunastic.erosion_era.block;

import com.lunastic.erosion_era.ErosionEraMod;
import com.lunastic.erosion_era.block.eroded.ErodedGrassBlock;
import net.minecraft.block.Block;

public class ErosionEraBlocks {

    public static final Block ERODED_GRASS_BLOCK = new ErodedGrassBlock();

    public static void register(){
        ErosionEraMod.LOGGER.info("Loading Erosion Era Blocks......");
    }
}
