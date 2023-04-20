package com.lunastic.erosion_era.item.basic;

import com.lunastic.erosion_era.ErosionEraMod;
import com.lunastic.erosion_era.item.ModItems;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BasicItemBase extends Item {

    /** 基础物品组 */
    public static final ItemGroup GROUP = FabricItemGroupBuilder.create(
            new Identifier(ErosionEraMod.NAMESPACE, "basic_group"))
            .icon(() -> new ItemStack(ModItems.TEST_ITEM))
            .build();

    /**
     * 基础物品总类
     * @param name 注册名称
     * @param settings 设定类
     */
    public BasicItemBase(String name, Item.Settings settings) {
        super(settings);
        Registry.register(Registry.ITEM, new Identifier(ErosionEraMod.NAMESPACE, name), this);
    }

    public BasicItemBase(String name){
        this(name, new FabricItemSettings()
                .group(BasicItemBase.GROUP));
    }
}
