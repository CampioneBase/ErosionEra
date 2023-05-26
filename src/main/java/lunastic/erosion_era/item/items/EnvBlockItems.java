package lunastic.erosion_era.item.items;

import lunastic.erosion_era.ErosionEraMod;
import lunastic.erosion_era.block.blocks.EnvBlocks;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class EnvBlockItems extends ErosionEraItems{
    public static final ItemGroup GROUP = FabricItemGroupBuilder
            .create(new Identifier(ErosionEraMod.ID, "erosion_env_block"))
            .icon(() -> new ItemStack(EnvBlockItems.SHIMMER_CORE))
            .build();

    public static final Item SHIMMER_CORE = register(EnvBlocks.SHIMMER_CORE);
    public static final Item SHIMMER_COL = register(EnvBlocks.SHIMMER_COL);


    private static Item register(Block block){
        return register(block, EnvBlockItems.GROUP);
    }

}
