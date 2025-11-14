package campionebase.erosionera;

import campionebase.erosionera.init.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ErosionEra.MODID)
public class ErosionEra {
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger(ErosionEra.class);
    public static final String MODID = "erosionera";


    public ErosionEra(FMLJavaModLoadingContext context) {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        init(context);
    }

    private void init(FMLJavaModLoadingContext ctx){
        IEventBus bus = ctx.getModEventBus();
        ErErBlocks.REGISTRY.register(bus);
        ErErItems.REGISTRY.register(bus);
        ErErEntities.REGISTRY.register(bus);
        ErErTabs.REGISTRY.register(bus);
        ErErAttributes.REGISTRY.register(bus);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
    }
}
