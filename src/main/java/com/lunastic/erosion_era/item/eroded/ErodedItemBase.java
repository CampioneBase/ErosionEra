package com.lunastic.erosion_era.item.eroded;

import com.lunastic.erosion_era.ErosionEraMod;
import com.lunastic.erosion_era.item.ModItems;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
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
     */
    public ErodedItemBase(String name) {
        super(new ErosionItemSetting());
        Registry.register(Registry.ITEM, new Identifier(ErosionEraMod.NAMESPACE, name), this);
    }

    /** 受侵蚀类物品设定类 */
    public static class ErosionItemSetting extends Item.Settings{
        public ErosionItemSetting(){
            super();
            this.group(ErodedItemBase.GROUP);
        }
    }
}
