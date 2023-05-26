package lunastic.erosion_era.item.items;

import lunastic.erosion_era.ErosionEraMod;
import net.minecraft.block.Block;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * 物品集合总类
 * @author Lunastic
 * @see ErodedBlockItems 侵蚀方块集合类
 */
public abstract class ErosionEraItems {

    // -- copy from Blocks.java
    protected static Item register(Block block, ItemGroup group) {
        return ErosionEraItems.register(new BlockItem(block, new Item.Settings().group(group)));
    }

    protected static Item register(BlockItem item) {
        return ErosionEraItems.register(item.getBlock(), item);
    }

    protected static Item register(Block block, Item item) {
        return ErosionEraItems.register(Registry.BLOCK.getId(block), item);
    }

    protected static Item register(String id, Item item) {
        return ErosionEraItems.register(new Identifier(ErosionEraMod.ID, id), item);
    }

    protected static Item register(Identifier id, Item item) {
        if (item instanceof BlockItem) {
            ((BlockItem)item).appendBlocks(Item.BLOCK_ITEMS, item);
        }
        return Registry.register(Registry.ITEM, id, item);
    }
    // -- end copy
}

