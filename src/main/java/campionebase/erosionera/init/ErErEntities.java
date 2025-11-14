package campionebase.erosionera.init;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.entity.ErodedWolf;
import campionebase.erosionera.entity.ErodedWolfMaster;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobType;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ErErEntities {
    /**
     * 受侵蚀的生物类型
     */
    public static final MobType ERODED = new MobType();

    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ErosionEra.MODID);

    public static final RegistryObject<EntityType<ErodedWolf>> ERODED_WOLF = register("eroded_wolf",
            EntityType.Builder.<ErodedWolf>of(ErodedWolf::new, MobCategory.MONSTER)
                    .setShouldReceiveVelocityUpdates(true)
                    .setTrackingRange(64).setUpdateInterval(3)
                    .setCustomClientFactory(ErodedWolf::new)
                    .sized(0.6f, 0.8f)
            );
    public static final RegistryObject<EntityType<ErodedWolfMaster>> ERODED_WOLF_Master = register("eroded_wolf_master",
            EntityType.Builder.<ErodedWolfMaster>of(ErodedWolfMaster::new, MobCategory.MONSTER)
                    .setShouldReceiveVelocityUpdates(true)
                    .setTrackingRange(64).setUpdateInterval(3)
                    .setCustomClientFactory(ErodedWolfMaster::new)
                    .sized(1.1f, 1.5f)
    );

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, EntityType.Builder<T> entityTypeBuilder) {
        return REGISTRY.register(name, () -> entityTypeBuilder.build(name));
    }

    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {

        });
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ERODED_WOLF.get(), ErodedWolf.createAttributes().build());
        event.put(ERODED_WOLF_Master.get(), ErodedWolfMaster.createAttributes().build());
    }
}
