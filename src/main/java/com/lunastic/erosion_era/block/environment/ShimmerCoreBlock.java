package com.lunastic.erosion_era.block.environment;

import com.lunastic.erosion_era.block.blocks.ErosionEraBlocks;
import net.minecraft.block.Material;

public class ShimmerCoreBlock extends EnvBlock {
    public ShimmerCoreBlock() {
        super(ErosionEraBlocks.Settings
                .of(Material.METAL)
                .eroded()
        );
    }
}
