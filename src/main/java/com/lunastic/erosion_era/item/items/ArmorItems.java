package com.lunastic.erosion_era.item.items;

import com.lunastic.erosion_era.ErosionEraMod;
import com.lunastic.erosion_era.item.armor.ArmorItemBase;
import com.lunastic.erosion_era.item.armor.ErosionEraArmorMaterials;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Block;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class ArmorItems extends ErosionEraItems{

    /** 装备组 */
    public static final ItemGroup GROUP = FabricItemGroupBuilder.create(
            new Identifier(ErosionEraMod.NAMESPACE, "armor_group"))
            .icon(() -> new ItemStack(ArmorItems.BASIC_CHEST))
            .build();


    public static final Item BASIC_HELMET = register("basic_helmet", new ArmorItemBase(ErosionEraArmorMaterials.BASIC_MATERIAL, EquipmentSlot.HEAD));
    public static final Item BASIC_CHEST = register("basic_chestplate", new ArmorItemBase(ErosionEraArmorMaterials.BASIC_MATERIAL, EquipmentSlot.CHEST));
    public static final Item BASIC_LEGS = register("basic_leggings", new ArmorItemBase(ErosionEraArmorMaterials.BASIC_MATERIAL, EquipmentSlot.LEGS));
    public static final Item BASIC_FEET = register("basic_boots", new ArmorItemBase(ErosionEraArmorMaterials.BASIC_MATERIAL, EquipmentSlot.FEET));

    private static Item register(Block block){
        return ErosionEraItems.register(block, ErodedBlockItems.GROUP);
    }

}
