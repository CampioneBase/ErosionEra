package lunastic.erosion_era.init;

import lunastic.erosion_era.ErosionEraMod;
import lunastic.erosion_era.effect.ErosionStatusEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.registry.Registry;

public class ErErStatusEffects {
    public static final StatusEffect EROSION_EFFECT = ErErStatusEffects.register("erosion", new ErosionStatusEffect());

    public static StatusEffect register(String id, StatusEffect effect){
        return Registry.register(Registry.STATUS_EFFECT, ErosionEraMod.identifier(id), effect);
    }

    public static void init(){
        ErosionEraMod.LOGGER.info("Effects Loading......");
    }
}
