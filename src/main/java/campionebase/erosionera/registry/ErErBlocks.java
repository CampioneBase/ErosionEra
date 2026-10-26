package campionebase.erosionera.registry;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.block.RuneCraftingTableBlock;
import campionebase.erosionera.data.lang.Translation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Translation(prefix = "block")
public class ErErBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ErosionEra.MODID);
    @Translation.Key("符文合成台")
    public static final RegistryObject<Block> RUNE_CRAFTING_TABLE = BLOCKS.register(
            "rune_crafting_table", RuneCraftingTableBlock::new
    );

}
