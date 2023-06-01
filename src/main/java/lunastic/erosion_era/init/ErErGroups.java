package lunastic.erosion_era.init;

import lunastic.erosion_era.ErosionEraMod;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ErErGroups {
    /** 装备组 */
    public static final ItemGroup ARMOR = FabricItemGroupBuilder.create(ErosionEraMod.identifier("armor"))
            .icon(() -> new ItemStack(ErErItems.BASIC_CHEST))
            .appendItems(list -> {
                list.add(new ItemStack(ErErItems.BASIC_HELMET));
                list.add(new ItemStack(ErErItems.BASIC_CHEST));
                list.add(new ItemStack(ErErItems.BASIC_LEGS));
                list.add(new ItemStack(ErErItems.BASIC_FEET));
            }).build();

    /** 基础物品组 */
    public static final ItemGroup BASIC = FabricItemGroupBuilder.create(ErosionEraMod.identifier("basic_group"))
            .icon(() -> new ItemStack(ErErItems.EROSION_DEBRIS))
            .appendItems(list -> {
                list.add(new ItemStack(ErErItems.EROSION_DEBRIS));
            }).build();


    /** 环境方块组 */
    public static final ItemGroup ENV_BLOCK = FabricItemGroupBuilder.create(ErosionEraMod.identifier("erosion_env_block"))
            .icon(() -> new ItemStack(ErErBlockItems.SHIMMER_CORE))
            .appendItems(list -> {
                list.add(new ItemStack(ErErBlockItems.SHIMMER_CORE));
                list.add(new ItemStack(ErErBlockItems.SHIMMER_PILLAR));
            }).build();

    /** 侵蚀方块组 */
    public static ItemGroup ERODED_BLOCK = FabricItemGroupBuilder.create(ErosionEraMod.identifier("eroded_block"))
            .icon(() -> new ItemStack(ErErBlockItems.ERODED_GRASS_BLOCK))
            .appendItems(list -> {
                list.add(new ItemStack(ErErBlockItems.SHIMMER_CORE));
                list.add(new ItemStack(ErErBlockItems.SHIMMER_PILLAR));
            }).build();

    /** 工具组 */
    public static final ItemGroup TOOL = FabricItemGroupBuilder.create(ErosionEraMod.identifier("tool_group"))
            .icon(() -> new ItemStack(ErErItems.BASIC_PICKAXE))
            .appendItems(list -> {
                list.add(new ItemStack(ErErItems.BASIC_PICKAXE));
            }).build();

    public static void init() {
        ErosionEraMod.LOGGER.info("Groups Loading......");
    }
}
