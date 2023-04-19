package com.lunastic.erosion_era.item.test;

import com.lunastic.erosion_era.item.eroded.AbstractErodedItem;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TestItem extends AbstractErodedItem {

    public TestItem(String name) {
        super(name);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {

        tooltip.add(new TranslatableText("item.erosion-era.test-item-description"));

    }
}
