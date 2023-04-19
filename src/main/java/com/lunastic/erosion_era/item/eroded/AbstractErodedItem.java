package com.lunastic.erosion_era.item.eroded;

import com.lunastic.erosion_era.ErosionEraMod;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.GlassBottleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

/** 受侵蚀类物品总类 */
public abstract class AbstractErodedItem extends Item {

    /** 受侵蚀物品组 */
    public static final ItemGroup GROUP = FabricItemGroupBuilder.create(
            new Identifier(ErosionEraMod.NAMESPACE, "eroded-group"))
            .icon(() -> new ItemStack(new GlassBottleItem(new FabricItemSettings())))
            .build();

    public AbstractErodedItem() {
        super(new ErosionSetting());
    }

    /**
     * 受侵蚀类物品总设定
     */
    public static class ErosionSetting extends Item.Settings{
        public ErosionSetting(){
            this.group(AbstractErodedItem.GROUP);
        }
    }
}
