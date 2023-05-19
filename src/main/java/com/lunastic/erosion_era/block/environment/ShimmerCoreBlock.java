package com.lunastic.erosion_era.block.environment;

import com.lunastic.erosion_era.tag.ErosionEraTags;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Material;

public class ShimmerCoreBlock extends EnvBlock {
    public ShimmerCoreBlock() {
        super("shimmer_core", FabricBlockSettings
                .of(Material.METAL)
                .breakByTool(ErosionEraTags.EROSION_TOOL)
        );
    }
}
