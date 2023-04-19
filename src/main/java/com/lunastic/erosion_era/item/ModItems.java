package com.lunastic.erosion_era.item;

import com.lunastic.erosion_era.ErosionEraMod;
import com.lunastic.erosion_era.item.armor.AbstractArmorItem;
import com.lunastic.erosion_era.item.armor.ErosionEraArmorMaterial;
import com.lunastic.erosion_era.item.test.TestItem;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;

// 统一物品注册和组注册
public class ModItems{

    public static final Item TEST_ITEM = new TestItem("test-item");

    public static final Item TEST_HELMET = new AbstractArmorItem("test-helmet", ErosionEraArmorMaterial.TEST_MATERIAL, EquipmentSlot.HEAD);
    public static final Item TEST_CHEST = new AbstractArmorItem("test-chest", ErosionEraArmorMaterial.TEST_MATERIAL, EquipmentSlot.CHEST);
    public static final Item TEST_LEGS = new AbstractArmorItem("test-legs", ErosionEraArmorMaterial.TEST_MATERIAL, EquipmentSlot.LEGS);
    public static final Item TEST_FEET = new AbstractArmorItem("test-feet", ErosionEraArmorMaterial.TEST_MATERIAL, EquipmentSlot.FEET);

    public static void register(){
        ErosionEraMod.LOGGER.info("加载物品");
    }
}


