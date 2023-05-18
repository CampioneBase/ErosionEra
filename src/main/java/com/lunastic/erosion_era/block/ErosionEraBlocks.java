package com.lunastic.erosion_era.block;

import com.lunastic.erosion_era.ErosionEraMod;
import com.lunastic.erosion_era.block.environment.ShimmerCoreBlock;
import com.lunastic.erosion_era.block.eroded.ErodedBlock;
import com.lunastic.erosion_era.block.eroded.ErodedGrassBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

public class ErosionEraBlocks {

    // --------- Eroded block           受侵蚀方块组
    public static final Block ERODED_GRASS_BLOCK = new ErodedGrassBlock();
    public static final Block ERODED_DIRT = new ErodedBlock("eroded_dirt", ErodedBlock.Settings.copyOf(Blocks.DIRT));

    // --------- Environment block      环境方块组
    public static final Block SHIMMER_CODE = new ShimmerCoreBlock();


    public static void registering(){
        ErosionEraMod.LOGGER.info("Loading Erosion Era Blocks......");
    }
}
