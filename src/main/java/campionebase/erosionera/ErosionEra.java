package campionebase.erosionera;

import campionebase.erosionera.registry.*;
import campionebase.erosionera.network.BioMachineryNetwork;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ErosionEra.MODID)
public class ErosionEra {
    public static final String MODID = "erosionera";
    public static final Logger LOGGER = LogManager.getLogger(ErosionEra.class);

    public ErosionEra(final FMLJavaModLoadingContext context) {
        this.init(context);
        context.getModEventBus().addListener(this::setup);
//        context.getModEventBus().addListener(this::enqueueIMC);
//        context.getModEventBus().addListener(this::processIMC);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void init(final FMLJavaModLoadingContext context){
        IEventBus bus = context.getModEventBus();
        ErErEntities.REGISTRY.register(bus);
        ErErBlocks.REGISTRY.register(bus);
        ErErBlockEntities.REGISTRY.register(bus);
        ErErItems.REGISTRY.register(bus);
        ErErTabs.REGISTRY.register(bus);
        ErErMenuTypes.REGISTRY.register(bus);
    }

    private void setup(final FMLCommonSetupEvent event) {
        BioMachineryNetwork.register();
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo("assets/erosionera", "helloworld", () -> {
            LOGGER.info("Hello world from the MDK");
            return "Hello world";
        });
    }

    private void processIMC(final InterModProcessEvent event) {
        // some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m -> m.messageSupplier().get()).
                collect(Collectors.toList()));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(final ServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
    }
}
