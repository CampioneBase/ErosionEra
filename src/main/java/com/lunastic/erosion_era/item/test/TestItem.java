package com.lunastic.erosion_era.item.test;

import com.lunastic.erosion_era.item.eroded.ErodedItemBase;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TestItem extends ErodedItemBase {

    public TestItem(String name) {
        super(name);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {

        tooltip.add(new TranslatableText("item.erosion_era.test_item_description"));

    }
}
