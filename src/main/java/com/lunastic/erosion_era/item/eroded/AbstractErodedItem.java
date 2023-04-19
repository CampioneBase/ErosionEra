package com.lunastic.erosion_era.item.eroded;

import com.lunastic.erosion_era.ErosionEraMod;
import com.lunastic.erosion_era.item.ModItems;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/** 受侵蚀类物品总类 */
public abstract class AbstractErodedItem extends Item {

    /** 受侵蚀物品组 */
    public static final ItemGroup GROUP = FabricItemGroupBuilder.create(
            new Identifier(ErosionEraMod.NAMESPACE, "eroded-group"))
            .icon(() -> new ItemStack(ModItems.TEST_ITEM))
            .build();

    public AbstractErodedItem(String name) {
        super(new ErosionSetting());
        Registry.register(Registry.ITEM, new Identifier(ErosionEraMod.NAMESPACE, name), this);
    }

    /**
     * 受侵蚀类物品总设定
     */
    public static class ErosionSetting extends Item.Settings{
        public ErosionSetting(){
            super();
            this.group(AbstractErodedItem.GROUP);
        }
    }
}
