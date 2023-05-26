package lunastic.erosion_era.item.items;

import lunastic.erosion_era.ErosionEraMod;
import lunastic.erosion_era.block.blocks.ErodedBlocks;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

/** 侵蚀方块集合类 */
public class ErodedBlockItems extends ErosionEraItems {

    /** 侵蚀方块组 */
    public static ItemGroup GROUP = FabricItemGroupBuilder
            .create(new Identifier(ErosionEraMod.ID, "eroded_block"))
            .icon(() -> new ItemStack(ErodedBlockItems.ERODED_GRASS_BLOCK))
            .build();

    public static Item ERODED_GRASS_BLOCK = register(ErodedBlocks.ERODED_GRASS_BLOCK);
    public static Item ERODED_DIRT = register(ErodedBlocks.ERODED_DIRT);

    private static Item register(Block block){
        return register(block, ErodedBlockItems.GROUP);
    }
}