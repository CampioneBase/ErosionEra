package lunastic.erosion_era.block.blocks;

import lunastic.erosion_era.block.environment.EnvBlock;
import lunastic.erosion_era.block.environment.ShimmerCoreBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;

/**
 * 环节方块集合类
 */
public class EnvBlocks extends ErosionEraBlocks{

    public static final Block SHIMMER_CORE = register("shimmer_core",
            new ShimmerCoreBlock());
    public static final Block SHIMMER_COL = register("shimmer_col",
            new EnvBlock(ErosionEraBlocks.Settings.of(Material.METAL)));
}
