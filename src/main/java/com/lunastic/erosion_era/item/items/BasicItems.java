package com.lunastic.erosion_era.item.items;

import com.lunastic.erosion_era.ErosionEraMod;
import com.lunastic.erosion_era.item.basic.BasicItemBase;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class BasicItems extends ErosionEraItems{
    /** 基础物品组 */
    public static final ItemGroup GROUP = FabricItemGroupBuilder.create(
            new Identifier(ErosionEraMod.NAMESPACE, "basic_group"))
            .icon(() -> new ItemStack(BasicItems.EROSION_DEBRIS))
            .build();

    public static final Item EROSION_DEBRIS = register("erosion_debris", new BasicItemBase());


}
