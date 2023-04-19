package com.lunastic.erosion_era.item;

import com.lunastic.erosion_era.ErosionEraMod;
import com.lunastic.erosion_era.item.armor.ArmorItemBase;
import com.lunastic.erosion_era.item.armor.ErosionEraArmorMaterials;
import com.lunastic.erosion_era.item.basic.ErosionDebris;
import com.lunastic.erosion_era.item.test.TestItem;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;

// 物品类单例集合
public class ModItems{

    public static final Item TEST_ITEM = new TestItem("test_item");

    // 基础组
    public static final Item EROSION_DEBRIS = new ErosionDebris();

    // 装备组
    public static final Item BASIC_HELMET = new ArmorItemBase("basic_helmet", ErosionEraArmorMaterials.Basic_MATERIAL, EquipmentSlot.HEAD);
    public static final Item BASIC_CHEST = new ArmorItemBase("basic_chestplate", ErosionEraArmorMaterials.Basic_MATERIAL, EquipmentSlot.CHEST);
    public static final Item BASIC_LEGS = new ArmorItemBase("basic_leggings", ErosionEraArmorMaterials.Basic_MATERIAL, EquipmentSlot.LEGS);
    public static final Item BASIC_FEET = new ArmorItemBase("basic_boots", ErosionEraArmorMaterials.Basic_MATERIAL, EquipmentSlot.FEET);

    public static void register(){
        ErosionEraMod.LOGGER.info("加载物品");
    }
}


