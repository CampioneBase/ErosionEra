package lunastic.erosion_era.init;

import lunastic.erosion_era.ErosionEraMod;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import java.util.function.Supplier;

public class ErErGroups {
    /** 装备组 */
    public static final ItemGroup ARMOR = create("armor_group", () -> new ItemStack(ErErItems.BASIC_CHEST));

    /** 基础物品组 */
    public static final ItemGroup BASIC = create("basic_group", () -> new ItemStack(ErErItems.EROSION_DEBRIS));

    /** 环境方块组 */
    public static final ItemGroup ENV_BLOCK = create("erosion_env_block", () -> new ItemStack(ErErBlocks.SHIMMER_CORE));

    /** 侵蚀方块组 */
    public static ItemGroup ERODED_BLOCK = create("eroded_block", () -> new ItemStack(ErErBlockItems.ERODED_GRASS_BLOCK));

    /** 工具组 */
    public static final ItemGroup TOOL = create("tool_group", () -> new ItemStack(ErErItems.BASIC_PICKAXE));

    public static ItemGroup create(String id, Supplier<ItemStack> icon){
        return FabricItemGroupBuilder.build(ErosionEraMod.identifier(id), icon);
    }

    public static void init() {
        ErosionEraMod.LOGGER.info("Groups Loading......");
    }
}
