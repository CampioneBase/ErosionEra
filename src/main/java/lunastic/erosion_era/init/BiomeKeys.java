package lunastic.erosion_era.init;

import lunastic.erosion_era.ErosionEraMod;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

public class BiomeKeys {
    public static RegistryKey<Biome> EROSION_AREA = RegistryKey.of(Registry.BIOME_KEY, ErosionEraMod.identifier("erosion_area"));
}
