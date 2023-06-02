package lunastic.erosion_era.init;

import lunastic.erosion_era.ErosionEraMod;
import lunastic.erosion_era.item.armor.ArmorItemBase;
import lunastic.erosion_era.item.armor.material.ErosionEraArmorMaterials;
import lunastic.erosion_era.item.tool.BasicPickaxeItem;
import lunastic.erosion_era.item.tool.ErosionEraToolMaterials;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.util.registry.Registry;

public class ErErItems {

    // -- equipment 装备
    // basic 基础
    public static final Item BASIC_HELMET = register("basic_helmet", new ArmorItemBase(ErosionEraArmorMaterials.BASIC_MATERIAL, EquipmentSlot.HEAD));
    public static final Item BASIC_CHEST = register("basic_chestplate", new ArmorItemBase(ErosionEraArmorMaterials.BASIC_MATERIAL, EquipmentSlot.CHEST));
    public static final Item BASIC_LEGS = register("basic_leggings", new ArmorItemBase(ErosionEraArmorMaterials.BASIC_MATERIAL, EquipmentSlot.LEGS));
    public static final Item BASIC_FEET = register("basic_boots", new ArmorItemBase(ErosionEraArmorMaterials.BASIC_MATERIAL, EquipmentSlot.FEET));

    // -- basic 初级
    public static final Item EROSION_DEBRIS = register("erosion_debris", new Item(new FabricItemSettings().group(ErErGroups.BASIC)));

    // -- tool 工具
    // basic 基础
    public static final Item BASIC_PICKAXE = register("basic_pickaxe", new BasicPickaxeItem(ErosionEraToolMaterials.BASIC_MATERIAL, 1, -2.8F));


    private static Item register(String id, Item item) {
        return Registry.register(Registry.ITEM, ErosionEraMod.identifier(id), item);
    }

    public static void init() {
        ErosionEraMod.LOGGER.info("Items Loading......");
    }
}


