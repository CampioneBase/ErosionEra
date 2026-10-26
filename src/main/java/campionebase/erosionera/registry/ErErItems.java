package campionebase.erosionera.registry;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.data.lang.Translation;
import campionebase.erosionera.item.MagicStoneItem;
import campionebase.erosionera.item.RuneStoneItem;
import campionebase.erosionera.item.SimpleWandItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Translation(prefix = "item")
public class ErErItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ErosionEra.MODID);
    public static final DeferredRegister<Item> BLOCKS = DeferredRegister.create(ForgeRegistries.ITEMS, ErosionEra.MODID);
    public static final DeferredRegister<Item> MAGIC = DeferredRegister.create(ForgeRegistries.ITEMS, ErosionEra.MODID);
    public static void register(IEventBus bus){
        ITEMS.register(bus);
        BLOCKS.register(bus);
        MAGIC.register(bus);
    }

    @Translation.Key("测试法杖")
    public static final RegistryObject<Item> SIMPLE_WAND = ITEMS.register("simple_wand", SimpleWandItem::new);
    @Translation.Key("符文石")
    public static final RegistryObject<RuneStoneItem> RUNE_STONE = MAGIC.register("rune_stone", RuneStoneItem::new);
    @Translation.Key("魔法石")
    public static final RegistryObject<Item> MAGIC_STONE = MAGIC.register("magic_stone", MagicStoneItem::new);


    //region =================== Block Item =====================
    private static RegistryObject<BlockItem> block(RegistryObject<Block> block, Item.Properties properties){
        return BLOCKS.register(block.getId().getPath(), () -> new BlockItem(block.get(), properties));
    }

    private static RegistryObject<BlockItem> block(RegistryObject<Block> block){
        return block(block, new Item.Properties());
    }
    public static final RegistryObject<BlockItem> RUNE_CRAFTING_TABLE = block(ErErBlocks.RUNE_CRAFTING_TABLE);
    //endregion

}
