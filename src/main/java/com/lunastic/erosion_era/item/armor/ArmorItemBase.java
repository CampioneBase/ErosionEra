package com.lunastic.erosion_era.item.armor;

import com.lunastic.erosion_era.ErosionEraMod;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/** 盔甲物品总类 */
public class ArmorItemBase extends ArmorItem {

    /**
     * 盔甲物品总类
     * @param name 盔甲注册名称
     * @param material 盔甲游戏材质
     * @param slot 盔甲部位
     * @param settings 设定类
     */
    public ArmorItemBase(String name, ArmorMaterial material, EquipmentSlot slot, Item.Settings settings) {
        super(material, slot, settings);
        Registry.register(Registry.ITEM, new Identifier(ErosionEraMod.NAMESPACE, name), this);
    }

    public ArmorItemBase(String name, ArmorMaterial material, EquipmentSlot slot){
        this(name, material, slot, new FabricItemSettings()
                .group(ErosionEraArmorMaterials.GROUP));
    }
}
