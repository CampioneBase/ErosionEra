package campionebase.erosionera.client.gui.panel;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.api.BioMachineType;
import campionebase.erosionera.api.IBioMachine;
import campionebase.erosionera.api.render.BioMachineRenderable;
import campionebase.erosionera.api.render.BioMachineRenderers;
import campionebase.erosionera.api.render.PanelRendererFactory;
import campionebase.erosionera.blockentity.BioBinBlockEntity;
import campionebase.erosionera.blockentity.BioBarrelBlockEntity;
import campionebase.erosionera.registry.ErErBlockEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(
        value = Dist.CLIENT,
        modid = ErosionEra.MODID,
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public final class PanelRenderers {
    public static final Logger LOGGER = LogManager.getLogger(PanelRenderers.class);
    private PanelRenderers(){}

    private static final Map<ResourceLocation, PanelRendererFactory> FACTORIES = new HashMap<>();
    // 占位符
    public static final PanelRendererFactory DEFAULT = nbt -> BioMachineRenderers.selectedText(
            Component.literal("No such info."),
            0xFFFFFFFF, 0xFFAAAAAA
    );

    private static BioMachineRenderable warning(String text){
        return BioMachineRenderers.text(Component.literal(text));
    }

    private static BioMachineRenderable error(String text){
        return BioMachineRenderers.text(Component.literal(text));
    }

    /** 通过 ID 注册 */
    public static void register(@NotNull ResourceLocation id, @NotNull PanelRendererFactory factory){
        FACTORIES.put(id, factory);
    }

    /**
     * 根据机械类别注册
     * @see IBioMachine#getId()
     */
    public static void register(@NotNull BioMachineType<?> type, @NotNull PanelRendererFactory factory){
        register(type.getId(), factory);
    }

    /**
     * 根据方块实体注册
     */
    public static void register(@NotNull RegistryObject<BlockEntityType<?>> registryObject, @NotNull PanelRendererFactory factory){
        register(registryObject.getId(), factory);
    }

    public static BioMachineRenderable create(CompoundTag customData){
        if (customData.contains(IBioMachine.TAG_ID)) {
            String idStr = customData.getString(IBioMachine.TAG_ID);
            if (!idStr.isEmpty()){
                try {
                    ResourceLocation id = ResourceLocation.parse(idStr);
                    PanelRendererFactory factory = FACTORIES.get(id);
                    if (factory == null)
                        return BioMachineRenderers.vbox(
                                warning("Miss panel renderer factory at id: " + idStr),
                                warning("Make sure to register factory successfully or use id correctly.")
                        );
                    else
                        return factory.create(customData);
                } catch (Exception e){
                    return error(e.getMessage());
                }
            }
        }
        return warning("Miss ID information when generating panel components");
    }

    @SubscribeEvent
    public static void registerPanelRenderer(FMLClientSetupEvent event){
        register(ErErBlockEntities.BIO_BARREL.getId(), BioBarrelBlockEntity::getRenderer);
        register(ErErBlockEntities.BIO_BIN.getId(), BioBinBlockEntity::getRenderer);
    }
}
