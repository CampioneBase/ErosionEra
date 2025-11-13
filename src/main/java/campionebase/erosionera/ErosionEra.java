package campionebase.erosionera;

import campionebase.erosionera.init.EEBlocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ErosionEra.MODID)
public class ErosionEra {
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger(ErosionEra.class);
    public static final String MODID = "erosion_era";


    public ErosionEra(FMLJavaModLoadingContext context) {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        init(context);
    }

    private void init(FMLJavaModLoadingContext ctx){
        IEventBus bus = ctx.getModEventBus();
        EEBlocks.REGISTRY.register(bus);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
    }
}
