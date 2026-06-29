package campionebase.erosionera.init;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.data.lang.Translation;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
@Translation(key = "item_group")
public class ErErTabs {
    public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ErosionEra.MODID);
    @Translation.ZH_CN("侵蚀国度")
    public static final RegistryObject<CreativeModeTab> MAIN = REGISTRY.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("item_group.erosionera.main"))
            .icon(() -> new ItemStack(ErErItems.BIOMETAL.get()))
            .displayItems((parameters, data) ->{
                data.accept(ErErItems.BIOMETAL.get());
                data.accept(ErErItems.BIO_CONTROLLER_BED.get());
            })
            .build()
    );
}
