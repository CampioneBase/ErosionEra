package com.lunastic.erosion_era.block;

import com.lunastic.erosion_era.ErosionEraMod;
import com.lunastic.erosion_era.block.environment.EnvBlock;
import com.lunastic.erosion_era.block.environment.ShimmerCoreBlock;
import com.lunastic.erosion_era.block.eroded.ErodedBlock;
import com.lunastic.erosion_era.block.eroded.ErodedGrassBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;

public class ErosionEraBlocks {

    // --------- Eroded block           受侵蚀方块组
    public static final Block ERODED_GRASS_BLOCK = new ErodedGrassBlock();
    public static final Block ERODED_DIRT = new ErodedBlock("eroded_dirt", ErodedBlock.Settings.copyOf(Blocks.DIRT));

    // --------- Environment block      环境方块组
    public static final Block SHIMMER_CORE = new ShimmerCoreBlock();
    public static final Block SHIMMER_COL = new EnvBlock("shimmer_col", ErodedBlock.Settings.of(Material.METAL));


    public static void registering(){
        ErosionEraMod.LOGGER.info("Loading Erosion Era Blocks......");
    }

    // todo: 重构注册方法，使用原版的模式
}
