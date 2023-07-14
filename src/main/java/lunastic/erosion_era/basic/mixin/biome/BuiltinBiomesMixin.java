package lunastic.erosion_era.basic.mixin.biome;

import lunastic.erosion_era.init.ErErBiomeKeys;
import lunastic.erosion_era.world.biome.BiomeHelper;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BuiltinBiomes.class)
public class BuiltinBiomesMixin {

    private static final Biome EROSION_AREA_BIOME = new Biome.Builder()
            .precipitation(Biome.Precipitation.RAIN)
            .temperature(0.8F)
            .temperatureModifier(Biome.TemperatureModifier.NONE)
            .downfall(0.4F)
            .effects(new BiomeEffects.Builder()
                    .waterColor(0x3f76e4)
                    .waterFogColor(0x050533)
                    .fogColor(0xc0d8ff)
                    .skyColor(0x77adff)
                    .build()
            )
            .spawnSettings(new SpawnSettings.Builder()
                    .creatureSpawnProbability(0.9F)
                    .build()
            )
            .generationSettings(BiomeHelper.erosionArea()
                    .build()
            )
            .build();

    @Inject(method = "getDefaultBiome", at = @At("HEAD"))
    private static void getErErBiomes(Registry<Biome> registry, CallbackInfoReturnable<RegistryEntry<Biome>> cir){
        BuiltinRegistries.add(BuiltinRegistries.BIOME, ErErBiomeKeys.EROSION_AREA, EROSION_AREA_BIOME);

    }
}
