package lunastic.erosion_era.util.mixin.biome.sorce.util;

import lunastic.erosion_era.init.BiomeKeys;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.biome.source.util.VanillaBiomeParameters;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VanillaBiomeParameters.class)
public abstract class VanillaBiomeParametersMixin {

    @Inject(method = "getRegularBiome", at = @At(value = "RETURN"), cancellable = true)
    private void injectGetRegularBiome(int temperature, int humidity, MultiNoiseUtil.ParameterRange weirdness, CallbackInfoReturnable<RegistryKey<Biome>> cir) {
        RegistryKey<Biome> result = cir.getReturnValue();
        cir.setReturnValue(Math.random() < 0.2 ? BiomeKeys.EROSION_AREA : result);
    }
}
