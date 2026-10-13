package campionebase.erosionera;

import campionebase.erosionera.registry.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ErosionEra.MODID)
public class ErosionEra {
    public static final String MODID = "erosionera";
    public static final Logger LOGGER = LogManager.getLogger(ErosionEra.class);

    public ErosionEra(final FMLJavaModLoadingContext context) {
        this.init(context);
        context.getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void init(final FMLJavaModLoadingContext context){
        IEventBus bus = context.getModEventBus();
        ErErEntities.REGISTER.register(bus);
        ErErFluids.register(bus);
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("{} common setup complete", MODID);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(final ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }
}
