package com.lunastic.erosion_era.item.armor;

import com.lunastic.erosion_era.ErosionEraMod;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/** 盔甲物品总类 */
public class ArmorItemBase extends ArmorItem {

    /** 装备组 */
    public static final ItemGroup GROUP = FabricItemGroupBuilder.create(
            new Identifier(ErosionEraMod.NAMESPACE, "armor_group"))
            .icon(() -> new ItemStack(Items.IRON_INGOT))
            .build();

    /**
     * 盔甲物品总类
     * @param name 盔甲注册名称
     * @param material 盔甲游戏材质
     * @param slot 盔甲部位
     */
    public ArmorItemBase(String name, ArmorMaterial material, EquipmentSlot slot) {
        super(material, slot, new ArmorItemSetting());
        Registry.register(Registry.ITEM, new Identifier(ErosionEraMod.NAMESPACE, name), this);
    }

    /** 盔甲设定类 */
    public static class ArmorItemSetting extends Item.Settings{
        public ArmorItemSetting(){
            super();
            this.group(ArmorItemBase.GROUP);
        }
    }
}
