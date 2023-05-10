package com.lunastic.erosion_era.block.eroded;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.client.render.RenderLayer;

public class ErodedGrassBlock extends ErodedBlockBase{
    public ErodedGrassBlock() {
        super("eroded_grass_block", FabricBlockSettings
                .copyOf(Blocks.GRASS_BLOCK)
                .requiresTool()
        );
        // 使用草方块的纹理，添加侵蚀颜色
        ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> 0xd412e0, this);
        ColorProviderRegistry.ITEM.register((itemStack, layer) -> 0xd412e0, this);
        // 处理半透明纹理
        BlockRenderLayerMap.INSTANCE.putBlock(this, RenderLayer.getTranslucent());
    }
}
