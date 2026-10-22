package campionebase.erosionera.registry;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.datagen.lang.Translation;
import campionebase.erosionera.item.RuneStoneItem;
import campionebase.erosionera.item.SimpleWandItem;
import campionebase.erosionera.item.SpellStoneItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Translation(prefix = "item")
public class ErErItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ErosionEra.MODID);
    public static final DeferredRegister<Item> MAGIC = DeferredRegister.create(ForgeRegistries.ITEMS, ErosionEra.MODID);
    public static void register(IEventBus bus){
        ITEMS.register(bus);
        MAGIC.register(bus);
    }

    @Translation.Key("测试法杖")
    public static final RegistryObject<Item> SIMPLE_WAND = ITEMS.register("simple_wand", SimpleWandItem::new);
    @Translation.Key("符文石")
    public static final RegistryObject<RuneStoneItem> RUNE_STONE = MAGIC.register("rune_stone", RuneStoneItem::new);
    public static final RegistryObject<Item> SPELL_STONE = MAGIC.register("spell_stone", SpellStoneItem::new);
}
