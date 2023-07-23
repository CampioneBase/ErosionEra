package lunastic.erosion_era.init;


import lunastic.erosion_era.ErosionEraMod;
import lunastic.erosion_era.block.environment.EnvBlock;
import lunastic.erosion_era.block.environment.ErosionVein;
import lunastic.erosion_era.block.environment.ShimmerCore;
import lunastic.erosion_era.block.environment.ShimmerPillarBlock;
import lunastic.erosion_era.block.eroded.ErodedGrassBlock;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.registry.Registry;

import java.util.function.Consumer;

public class Blocks {

    // Environment Block 环境方块组
    public static final Block SHIMMER_CORE = register("shimmer_core", new ShimmerCore());
    public static final Block SHIMMER_PILLAR = register("shimmer_pillar", new ShimmerPillarBlock());
    public static final Block EROSION_VEIN = register("erosion_vein", new ErosionVein(FabricBlockSettings.copyOf(net.minecraft.block.Blocks.SCULK_VEIN)), BlockHandler.Translucent);

    // Eroded Block 受侵蚀方块组
    public static final Block ERODED_GRASS_BLOCK = register("eroded_grass_block", new ErodedGrassBlock(), BlockHandler.Translucent);
    public static final Block ERODED_DIRT = register("eroded_dirt", new Block(FabricBlockSettings.copyOf(net.minecraft.block.Blocks.DIRT)), BlockHandler.Translucent);


    /**
     * @param id 注册名称
     * @param block 注册方块
     * @return 注册方块实例
     */

    private static Block register(String id, Block block){
        return Registry.register(Registry.BLOCK, ErosionEraMod.identifier(id), block);
    }

    /**
     * @param id 注册名称
     * @param block 注册方块
     * @param handlers 方块处理器
     * @return 注册方块实例
     */
    private static Block register(String id, Block block, BlockHandler... handlers){
        for (BlockHandler handler: handlers) handler.consumer.accept(block);
        return Registry.register(Registry.BLOCK, ErosionEraMod.identifier(id), block);
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
