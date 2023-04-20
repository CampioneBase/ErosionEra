package com.lunastic.erosion_era.item.eroded;

import com.lunastic.erosion_era.ErosionEraMod;
import com.lunastic.erosion_era.item.ModItems;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/** 受侵蚀类物品总类 */
public class ErodedItemBase extends Item {

    /** 受侵蚀物品组 */
    public static final ItemGroup GROUP = FabricItemGroupBuilder.create(
            new Identifier(ErosionEraMod.NAMESPACE, "eroded_group"))
            .icon(() -> new ItemStack(ModItems.TEST_ITEM))
            .build();

    /**
     * 受侵蚀物品总类
     * @param name 注册名称
     * @param settings 设定类
     */
    public ErodedItemBase(String name, Item.Settings settings) {
        super(settings);
        Registry.register(Registry.ITEM, new Identifier(ErosionEraMod.NAMESPACE, name), this);
    }

    public ErodedItemBase(String name) {
        this(name, new FabricItemSettings()
                .group(ErodedItemBase.GROUP));
    }
}
