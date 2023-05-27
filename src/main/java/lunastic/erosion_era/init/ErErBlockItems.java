package lunastic.erosion_era.init;

import lunastic.erosion_era.ErosionEraMod;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.registry.Registry;

public class ErErBlockItems {

    // -- environment 环境方块
    // shimmer 微光
    public static final Item SHIMMER_CORE = register(ErErBlocks.SHIMMER_CORE, ErErGroups.ENV_BLOCK);
    public static final Item SHIMMER_COL = register(ErErBlocks.SHIMMER_COL, ErErGroups.ENV_BLOCK);

    // -- eroded 侵蚀方块
    public static Item ERODED_GRASS_BLOCK = register(ErErBlocks.ERODED_GRASS_BLOCK, ErErGroups.ERODED_BLOCK);
    public static Item ERODED_DIRT = register(ErErBlocks.ERODED_DIRT, ErErGroups.ERODED_BLOCK);


    protected static Item register(Block block, ItemGroup group) {
        BlockItem blockItem = new BlockItem(block, new Item.Settings().group(group));
        return Registry.register(Registry.ITEM, Registry.BLOCK.getId(block), blockItem);
    }

    public static void init() {
        ErosionEraMod.LOGGER.info("Block-Items Loading......");
    }
}
