package campionebase.erosionera.init;

import campionebase.erosionera.ErosionEra;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ErErAttributes {
    public static DeferredRegister<Attribute> REGISTRY = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, ErosionEra.MODID);
    public static final RegistryObject<Attribute> EROSION_VALUE =
            REGISTRY.register("erosion_value",
                    () -> new RangedAttribute("attribute.erosion_era.erosion_value",
                            0, 0, 10000)
                            .setSyncable(true));
    public static final RegistryObject<Attribute> EROSION_LEVEL =
            REGISTRY.register("erosion_level",
                    () -> new RangedAttribute("attribute.erosion_era.erosion_level",
                            0, 0, 5)
                            .setSyncable(true));


    @SubscribeEvent
    public static void addAttributes(EntityAttributeModificationEvent event) {
        event.getTypes().forEach(entity -> event.add(entity, EROSION_VALUE.get()));
        event.getTypes().forEach(entity -> event.add(entity, EROSION_LEVEL.get()));
    }

    @Mod.EventBusSubscriber
    public static class PlayerAttributesSync {
        @SubscribeEvent
        public static void playerClone(PlayerEvent.Clone event) {
            Player oldPlayer = event.getOriginal();
            Player newPlayer = event.getEntity();
            attrClone(oldPlayer, newPlayer, EROSION_VALUE.get());
            attrClone(oldPlayer, newPlayer, EROSION_LEVEL.get());
        }

        private static void attrClone(Player oldPlayer, Player newPlayer, Attribute attr){
            newPlayer.getAttribute(attr).setBaseValue(oldPlayer.getAttribute(attr).getBaseValue());
        }
    }

}
