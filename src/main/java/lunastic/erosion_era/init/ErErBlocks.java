package lunastic.erosion_era.init;


import lunastic.erosion_era.ErosionEraMod;
import lunastic.erosion_era.block.environment.EnvBlock;
import lunastic.erosion_era.block.environment.ShimmerCoreBlock;
import lunastic.erosion_era.block.eroded.ErodedGrassBlock;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.registry.Registry;

import java.util.function.Consumer;

public class ErErBlocks {

    // Environment Block 环境方块组
    public static final Block SHIMMER_CORE = register("shimmer_core", new ShimmerCoreBlock());
    public static final Block SHIMMER_COL = register("shimmer_col", new EnvBlock(FabricBlockSettings.of(Material.METAL)));

    // Eroded Block 受侵蚀方块组
    public static final Block ERODED_GRASS_BLOCK = register("eroded_grass_block", new ErodedGrassBlock(), BlockHandler.Translucent);
    public static final Block ERODED_DIRT = register("eroded_dirt", new Block(FabricBlockSettings.copyOf(Blocks.DIRT)), BlockHandler.Translucent);


    /**
     * @param name 注册名称
     * @param block 注册方块
     * @return 注册方块实例
     */

    private static Block register(String name, Block block){
        return Registry.register(Registry.BLOCK, ErosionEraMod.identifier(name), block);
    }

    /**
     * @param name 注册名称
     * @param block 注册方块
     * @param handlers 方块处理器
     * @return 注册方块实例
     */
    private static Block register(String name, Block block, BlockHandler... handlers){
        for (BlockHandler handler: handlers) handler.consumer.accept(block);
        return Registry.register(Registry.BLOCK, ErosionEraMod.identifier(name), block);
    }

    private enum BlockHandler {
        Translucent(block -> BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getTranslucent()));

        public Consumer<Block> consumer;
        BlockHandler(Consumer<Block> consumer){
            this.consumer = consumer;
        }
    }

    public static void init() {
        ErosionEraMod.LOGGER.info("Blocks Loading......");
    }
}
