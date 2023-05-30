package lunastic.erosion_era.init;

import lunastic.erosion_era.ErosionEraMod;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import java.util.function.Supplier;

public class ErErGroups {
    /** 装备组 */
    public static final ItemGroup ARMOR = FabricItemGroup.builder(ErosionEraMod.identifier("armor_group"))
            .icon(() -> new ItemStack(ErErItems.BASIC_CHEST))
            .entries((displayContext, entries) -> {
                entries.add(ErErItems.BASIC_HELMET);
                entries.add(ErErItems.BASIC_CHEST);
                entries.add(ErErItems.BASIC_LEGS);
                entries.add(ErErItems.BASIC_FEET);
            }).build();

    /** 基础物品组 */
    public static final ItemGroup BASIC = FabricItemGroup.builder(ErosionEraMod.identifier("basic_group"))
            .icon(() -> new ItemStack(ErErItems.EROSION_DEBRIS))
            .entries((displayContext, entries) -> {
                entries.add(ErErItems.EROSION_DEBRIS);
            }).build();


    /** 环境方块组 */
    public static final ItemGroup ENV_BLOCK = FabricItemGroup.builder(ErosionEraMod.identifier("erosion_env_block"))
            .icon(() -> new ItemStack(ErErBlockItems.SHIMMER_CORE))
            .entries((displayContext, entries) -> {
                entries.add(ErErBlockItems.SHIMMER_CORE);
                entries.add(ErErBlockItems.SHIMMER_COL);
            }).build();

    /** 侵蚀方块组 */
    public static ItemGroup ERODED_BLOCK = FabricItemGroup.builder(ErosionEraMod.identifier("eroded_block"))
            .icon(() -> new ItemStack(ErErBlockItems.ERODED_GRASS_BLOCK))
            .entries((displayContext, entries) -> {
                entries.add(ErErBlockItems.ERODED_GRASS_BLOCK);
                entries.add(ErErBlockItems.ERODED_DIRT);
            }).build();

    /** 工具组 */
    public static final ItemGroup TOOL = FabricItemGroup.builder(ErosionEraMod.identifier("tool_group"))
            .icon(() -> new ItemStack(ErErItems.BASIC_PICKAXE))
            .entries((displayContext, entries) -> {
                entries.add(ErErItems.BASIC_PICKAXE);
            }).build();

    public static void init() {
        ErosionEraMod.LOGGER.info("Groups Loading......");
    }
}
