package com.lunastic.erosion_era.block.blocks;

import com.lunastic.erosion_era.block.eroded.ErodedGrassBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

public class ErodedBlocks extends ErosionEraBlocks{
    // --------- Eroded block           受侵蚀方块组
    public static final Block ERODED_GRASS_BLOCK = register("eroded_grass_block",
            new ErodedGrassBlock(),
            BlockHandler.Translucent);
    public static final Block ERODED_DIRT = register("eroded_dirt",
            new Block(Settings.copyOf(Blocks.DIRT).eroded()),
            BlockHandler.Translucent);


}
