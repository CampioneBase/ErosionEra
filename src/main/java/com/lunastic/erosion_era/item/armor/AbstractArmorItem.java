package com.lunastic.erosion_era.item.armor;

import com.lunastic.erosion_era.ErosionEraMod;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class AbstractArmorItem extends ArmorItem {

    /** 装备组 */
    public static final ItemGroup GROUP = FabricItemGroupBuilder.create(
            new Identifier(ErosionEraMod.NAMESPACE, "equipment-group"))
            .icon(() -> new ItemStack(Items.IRON_INGOT))
            .build();

    public AbstractArmorItem(String name, ArmorMaterial material, EquipmentSlot slot) {
        super(material, slot, new ArmorSetting());
        Registry.register(Registry.ITEM, new Identifier(ErosionEraMod.NAMESPACE, name), this);
    }

    public static class ArmorSetting extends Item.Settings{
        public ArmorSetting(){
            super();
            this.group(AbstractArmorItem.GROUP);
        }
    }
}
