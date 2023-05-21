package com.lunastic.erosion_era.item.items;

import com.lunastic.erosion_era.ErosionEraMod;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BasicItems extends ErosionEraItems{
    /** 基础物品组 */
    public static final ItemGroup GROUP = FabricItemGroupBuilder.create(
            new Identifier(ErosionEraMod.NAMESPACE, "basic_group"))
            .icon(() -> new ItemStack(BasicItems.EROSION_DEBRIS))
            .build();

    public static final Item EROSION_DEBRIS =
            register("erosion_debris", new Item(new FabricItemSettings().group(GROUP)));

    public static Item register(String name, Item item){
        return Registry.register(Registry.ITEM, new Identifier(ErosionEraMod.NAMESPACE, name), item);
    }

}
