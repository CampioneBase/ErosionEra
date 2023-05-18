package com.lunastic.erosion_era.block.eroded;

import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.block.*;

public class ErodedGrassBlock extends ErodedBlock {
    public ErodedGrassBlock() {
        super("eroded_grass_block", ErodedGrassBlock.Settings
                .copyOf(Blocks.GRASS_BLOCK)
        );
        // 使用草方块的纹理，添加侵蚀颜色
        ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> 8368696, this);
        ColorProviderRegistry.ITEM.register((itemStack, layer) -> 8368696, this);
    }
}

