package com.lunastic.erosion_era.block.eroded;

import com.lunastic.erosion_era.ErosionEraMod;
import com.lunastic.erosion_era.item.ErosionEraItems;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ErodedBlockBase extends Block {

    public static final ItemGroup GROUP = FabricItemGroupBuilder.create(
            new Identifier(ErosionEraMod.NAMESPACE, "eroded_block"))
            .icon(() -> new ItemStack(ErosionEraItems.EROSION_DEBRIS))
            .build();

    public ErodedBlockBase(String name, Settings settings) {
        super(settings);
        Registry.register(Registry.BLOCK, new Identifier(ErosionEraMod.NAMESPACE, name), this);
        Registry.register(Registry.ITEM, new Identifier(ErosionEraMod.NAMESPACE, name), new BlockItem(this,
                new FabricItemSettings().group(ErodedBlockBase.GROUP)
        ));
    }
}
