package com.lunastic.erosion_era.item.items;

import com.lunastic.erosion_era.ErosionEraMod;
import com.lunastic.erosion_era.item.armor.ArmorItemBase;
import com.lunastic.erosion_era.item.armor.ErosionEraArmorMaterials;
import com.lunastic.erosion_era.item.basic.ErosionDebris;
import com.lunastic.erosion_era.item.tool.BasicPickaxeItem;
import com.lunastic.erosion_era.item.tool.ErosionEraToolMaterials;
import net.minecraft.block.Block;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

/**
 * 物品集合总类
 * @author Lunastic
 * @see ErodedBlockItems 侵蚀方块集合类
 */
public abstract class ErosionEraItems {

    public static void registering(){
        ErosionEraMod.LOGGER.info("Loading Erosion Era Items......");
    }

    // -- copy from Blocks.java
    protected static Item register(Block block, ItemGroup group) {
        return ErosionEraItems.register(new BlockItem(block, new Item.Settings().group(group)));
    }

    protected static Item register(BlockItem item) {
        return ErosionEraItems.register(item.getBlock(), item);
    }

    protected static Item register(Block block, Item item) {
        return ErosionEraItems.register(Registry.BLOCK.getId(block), item);
    }

    protected static Item register(String id, Item item) {
        return ErosionEraItems.register(new Identifier(ErosionEraMod.NAMESPACE, id), item);
    }

    protected static Item register(Identifier id, Item item) {
        if (item instanceof BlockItem) {
            ((BlockItem)item).appendBlocks(Item.BLOCK_ITEMS, item);
        }
        return Registry.register(Registry.ITEM, id, item);
    }
    // -- end copy
}


