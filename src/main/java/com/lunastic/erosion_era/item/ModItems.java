package com.lunastic.erosion_era.item;

import com.lunastic.erosion_era.ErosionEraMod;
import com.lunastic.erosion_era.item.test.TestItem;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

// 统一物品注册和组注册
public class ModItems{

    public static final Item TEST_ITEM;

    static {
        TEST_ITEM = Registry.register(Registry.ITEM, new Identifier("erosion-era", "test-item") , new TestItem());
    }

    public static void load(){
        ErosionEraMod.LOGGER.info("加载物品");
    }
}

