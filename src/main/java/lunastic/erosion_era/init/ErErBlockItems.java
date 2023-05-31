package lunastic.erosion_era.init;

import lunastic.erosion_era.ErosionEraMod;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

public class ErErBlockItems {

    // -- environment 环境方块
    // shimmer 微光
    public static final Item SHIMMER_CORE = register(ErErBlocks.SHIMMER_CORE);
    public static final Item SHIMMER_PILLAR = register(ErErBlocks.SHIMMER_PILLAR);

    // -- eroded 侵蚀方块
    public static Item ERODED_GRASS_BLOCK = register(ErErBlocks.ERODED_GRASS_BLOCK);
    public static Item ERODED_DIRT = register(ErErBlocks.ERODED_DIRT);


    protected static Item register(Block block) {
        BlockItem blockItem = new BlockItem(block, new FabricItemSettings());
        return Registry.register(Registry.ITEM, Registry.BLOCK.getId(block), blockItem);
    }

    public static void init() {
        ErosionEraMod.LOGGER.info("Block-Items Loading......");
    }
}
