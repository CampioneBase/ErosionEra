package lunastic.erosion_era.init;

import lunastic.erosion_era.ErosionEraMod;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.registry.Registry;

public class BlockItems {

    // -- environment 环境方块
    // shimmer 微光
    public static final Item SHIMMER_CORE = register(Blocks.SHIMMER_CORE, ItemGroups.ENV_BLOCK);
    public static final Item SHIMMER_PILLAR = register(Blocks.SHIMMER_PILLAR, ItemGroups.ENV_BLOCK);
    public static final Item EROSION_VEIN = register(Blocks.EROSION_VEIN, ItemGroups.ENV_BLOCK);

    // -- eroded 侵蚀方块
    public static Item ERODED_GRASS_BLOCK = register(Blocks.ERODED_GRASS_BLOCK, ItemGroups.ERODED_BLOCK);
    public static Item ERODED_DIRT = register(Blocks.ERODED_DIRT, ItemGroups.ERODED_BLOCK);


    protected static Item register(Block block, ItemGroup group) {
        BlockItem blockItem = new BlockItem(block, new FabricItemSettings().group(group));
        return Registry.register(Registry.ITEM, Registry.BLOCK.getId(block), blockItem);
    }

    public static void init() {
        ErosionEraMod.LOGGER.info("Block-Items Loading......");
    }
}
