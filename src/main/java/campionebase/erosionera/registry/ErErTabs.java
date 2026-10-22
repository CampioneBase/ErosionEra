package campionebase.erosionera.registry;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.datagen.lang.Translation;
import campionebase.erosionera.item.RuneStoneItem;
import campionebase.erosionera.magic.spell.RuneEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
@Translation(prefix = "item_group")
public class ErErTabs {
    public static final DeferredRegister<CreativeModeTab> REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ErosionEra.MODID);
    @Translation.Key("侵蚀国度")
    public static final RegistryObject<CreativeModeTab> MAIN = REGISTER.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("item_group.erosionera.main"))
            .displayItems((parameters, data) -> {
                ErErItems.ITEMS.getEntries().forEach(r -> data.accept(r.get()));
                ErErFluids.ITEMS.getEntries().forEach(r -> data.accept(r.get()));
            })
            .build()
    );
    @Translation.Key("侵蚀国度：符文石")
    public static final RegistryObject<CreativeModeTab> RUNE = REGISTER.register("rune", () -> CreativeModeTab.builder()
            .title(Component.translatable("item_group.erosionera.rune"))
            .icon(() -> ErErItems.RUNE_STONE.get().getDefaultInstance())
            .displayItems((parameters, data) -> {
                data.accept(ErErItems.RUNE_STONE.get());
                // 每有一个符文 ID 创建一个对应符文石
                RuneEntry.idView().forEach(id -> data.accept(ErErItems.RUNE_STONE.get().getInstanceWithRune(id)));
            })
            .build()
    );

    @SubscribeEvent
    public static void addItemToVanillaTabs(BuildCreativeModeTabContentsEvent event){

    }
}
