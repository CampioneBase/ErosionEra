package lunastic.erosion_era.init;

import lunastic.erosion_era.ErosionEraMod;
import net.minecraft.block.Block;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.Registry;

public final class Tags {
    public static final TagKey<Block> EROSION_ENV = Tags.of("erosion_env");

    private static TagKey<Block> of(String id) {
        return TagKey.of(Registry.BLOCK_KEY, ErosionEraMod.identifier(id));
    }
}
