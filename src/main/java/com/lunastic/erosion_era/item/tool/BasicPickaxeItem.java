package com.lunastic.erosion_era.item.tool;

import com.lunastic.erosion_era.ErosionEraMod;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BasicPickaxeItem extends PickaxeItem {

    public BasicPickaxeItem(String name,
                               ToolMaterial material,
                               int attackDamage,
                               float attackSpeed,
                               Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
        Registry.register(Registry.ITEM, new Identifier(ErosionEraMod.NAMESPACE, name), this);
    }

    public BasicPickaxeItem(String name,
                               ToolMaterial material,
                               int attackDamage,
                               float attackSpeed) {
        this(name, material, attackDamage, attackSpeed, new FabricItemSettings()
                .group(ErosionEraToolMaterials.GROUP)
        );
    }
}
