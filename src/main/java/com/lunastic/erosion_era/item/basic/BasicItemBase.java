package com.lunastic.erosion_era.item.basic;

import com.lunastic.erosion_era.ErosionEraMod;
import com.lunastic.erosion_era.item.items.BasicItems;
import com.lunastic.erosion_era.item.items.ErosionEraItems;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BasicItemBase extends Item {


    /**
     * 基础物品总类
     * @param settings 设定类
     */
    public BasicItemBase(Item.Settings settings) {
        super(settings);
    }

    public BasicItemBase(){
        this(new FabricItemSettings()
                .group(BasicItems.GROUP));
    }
}
