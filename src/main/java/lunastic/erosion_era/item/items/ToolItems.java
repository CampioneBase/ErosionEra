package lunastic.erosion_era.item.items;

import lunastic.erosion_era.ErosionEraMod;
import lunastic.erosion_era.item.tool.BasicPickaxeItem;
import lunastic.erosion_era.item.tool.ErosionEraToolMaterials;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class ToolItems extends ErosionEraItems {


    // 工具组
    public static final ItemGroup GROUP = FabricItemGroupBuilder.create(
            new Identifier(ErosionEraMod.ID, "tool_group"))
            .icon(() -> new ItemStack(ToolItems.BASIC_PICKAXE))
            .build();

    public static final Item BASIC_PICKAXE = register("basic_pickaxe", new BasicPickaxeItem(ErosionEraToolMaterials.BASIC_MATERIAL, 1, -2.8F));
}
