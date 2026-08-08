package campionebase.erosionera.registry;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.data.lang.Translation;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
@Translation(key = "item_group")
public class ErErTabs {
    public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ErosionEra.MODID);
    @Translation.ZH_CN("侵蚀国度：活体机械")
    public static final RegistryObject<CreativeModeTab> BIO_MACHINERY = REGISTRY.register("bio_machinery", () -> CreativeModeTab.builder()
            .title(Component.translatable("item_group.erosionera.bio_machinery"))
            .icon(() -> new ItemStack(ErErItems.BIO_METAL.get()))
            .displayItems((parameters, data) ->{
                data.accept(ErErItems.BIO_METAL.get());
                data.accept(ErErItems.BIO_CONTROLLER_BED.get());
                data.accept(ErErItems.BIO_CONTROLLER.get());
                data.accept(ErErItems.BIO_WIRE.get());
                data.accept(ErErItems.BIO_CONNECTOR.get());
                data.accept(ErErItems.BIO_NUTRITION_TANK.get());
                data.accept(ErErItems.BIO_CAMERA.get());
                data.accept(ErErItems.BIO_REDSTONE.get());
            })
            .build()
    );
}
