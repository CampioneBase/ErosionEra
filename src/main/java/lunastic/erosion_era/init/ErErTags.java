package lunastic.erosion_era.init;

import lunastic.erosion_era.ErosionEraMod;
import net.minecraft.block.Block;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class ErErTags {
    public static final TagKey<Block> EROSION_ENV = ErErTags.of("erosion_env");

    private static TagKey<Block> of(String id) {
        return TagKey.of(Registry.BLOCK_KEY, ErosionEraMod.identifier(id));
    }
}
