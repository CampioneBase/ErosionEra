package com.lunastic.erosion_era.tag;

import com.lunastic.erosion_era.ErosionEraMod;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public final class ErosionEraTags {
    // 侵蚀方块标签
    public static final Tag<Block> ERODED_BLOCK = TagRegistry.block(new Identifier(ErosionEraMod.NAMESPACE, "eroded_block"));
    // 侵蚀工具标签
    public static final Tag<Item> EROSION_TOOL = TagRegistry.item(new Identifier(ErosionEraMod.NAMESPACE, "erosion_tool"));
}
