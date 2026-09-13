package campionebase.erosionera.client.gui.panel;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.api.BioMachineType;
import campionebase.erosionera.api.IBioMachine;
import campionebase.erosionera.registry.BioMachineTypes;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Mod.EventBusSubscriber(
        modid = ErosionEra.MODID,
        value = Dist.CLIENT,
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public class BioMachinePanels {
    private static final Logger LOGGER = LogManager.getLogger(BioMachinePanels.class);
    private static final Map<BioMachineType<?>, PanelFactory<?>> FACTORIES = new HashMap<>();

    @FunctionalInterface
    public interface PanelFactory<M extends IBioMachine> {
        AbstractBioMachinePanel<M> create(BioMachineType<M> type, Screen screen);
    }

    public static <M extends IBioMachine> void register(BioMachineType<M> type, PanelFactory<M> factory) {
        FACTORIES.put(type, factory);
    }

    public static boolean hasPanel(BioMachineType<?> type) {
        return FACTORIES.containsKey(type);
    }

    public static <M extends IBioMachine> @Nullable AbstractBioMachinePanel<M> createPanel(BioMachineType<M> type, Screen screen) {
        @SuppressWarnings("unchecked")
        PanelFactory<M> factory = (PanelFactory<M>) FACTORIES.get(type);
        if (factory == null) {
            LOGGER.warn("BioMachineType({}) has not panel factory", type.getId().toString());
            return null;
        }
        return factory.create(type, screen);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event){
        event.enqueueWork(() -> {
            register(BioMachineTypes.CAMERA.get(), CameraPanel::new);
        });
    }
}
