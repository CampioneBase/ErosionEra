package com.lunastic.erosion_era.item;

import com.lunastic.erosion_era.ErosionEraMod;
import com.lunastic.erosion_era.item.armor.ArmorItemBase;
import com.lunastic.erosion_era.item.armor.ErosionEraArmorMaterials;
import com.lunastic.erosion_era.item.basic.ErosionDebris;
import com.lunastic.erosion_era.item.tool.BasicPickaxeItem;
import com.lunastic.erosion_era.item.tool.ErosionEraToolMaterials;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;

// 物品类单例集合
public class ErosionEraItems {

    // 基础组
    public static final Item EROSION_DEBRIS = new ErosionDebris();

    // 装备组
    public static final Item BASIC_HELMET = new ArmorItemBase("basic_helmet", ErosionEraArmorMaterials.BASIC_MATERIAL, EquipmentSlot.HEAD);
    public static final Item BASIC_CHEST = new ArmorItemBase("basic_chestplate", ErosionEraArmorMaterials.BASIC_MATERIAL, EquipmentSlot.CHEST);
    public static final Item BASIC_LEGS = new ArmorItemBase("basic_leggings", ErosionEraArmorMaterials.BASIC_MATERIAL, EquipmentSlot.LEGS);
    public static final Item BASIC_FEET = new ArmorItemBase("basic_boots", ErosionEraArmorMaterials.BASIC_MATERIAL, EquipmentSlot.FEET);

    // 工具组
    public static final Item BASIC_PICKAXE = new BasicPickaxeItem("basic_pickaxe", ErosionEraToolMaterials.BASIC_MATERIAL, 1, -2.8F);

    public static void registering(){
        ErosionEraMod.LOGGER.info("Loading Erosion Era Items......");
    }
}


