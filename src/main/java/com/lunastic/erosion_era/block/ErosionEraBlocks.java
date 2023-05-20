package com.lunastic.erosion_era.block;

import com.lunastic.erosion_era.ErosionEraMod;
import com.lunastic.erosion_era.block.environment.EnvBlock;
import com.lunastic.erosion_era.block.environment.ShimmerCoreBlock;
import com.lunastic.erosion_era.block.eroded.ErodedBlock;
import com.lunastic.erosion_era.block.eroded.ErodedGrassBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class ErosionEraBlocks {

    // --------- Eroded block           受侵蚀方块组
    public static final Block ERODED_GRASS_BLOCK = ErosionEraBlocks.register("eroded_grass_block", new ErodedGrassBlock());
    public static final Block ERODED_DIRT = ErosionEraBlocks.register("eroded_dirt", new ErodedBlock(ErodedBlock.Settings.copyOf(Blocks.DIRT)));

    // --------- Environment block      环境方块组
    public static final Block SHIMMER_CORE = new ShimmerCoreBlock();
    public static final Block SHIMMER_COL = new EnvBlock("shimmer_col", ErodedBlock.Settings.of(Material.METAL));


    public static void registering(){
        ErosionEraMod.LOGGER.info("Loading Erosion Era Blocks......");
    }

    /**
     * @param name 注册名称
     * @param block 注册方块
     * @return 注册方块实例
     */

    public static Block register(String name, Block block){
        return Registry.register(Registry.BLOCK, new Identifier(ErosionEraMod.NAMESPACE, name), block);
    }
}
