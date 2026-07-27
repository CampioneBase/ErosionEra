package campionebase.erosionera.registry;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.client.screen.BioControllerScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(
        modid = ErosionEra.MODID,
        bus = Mod.EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT
)
public class ErErMenuScreens {
    @SubscribeEvent
    public static void RegisterScreens(FMLClientSetupEvent event){
        event.enqueueWork(() -> {
            MenuScreens.register(ErErMenuTypes.BIO_CONTROLLER_MENU.get(), BioControllerScreen::new);
        });
    }
}
