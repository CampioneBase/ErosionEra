package lunastic.erosion_era.block.blocks;

import lunastic.erosion_era.ErosionEraMod;
import lunastic.erosion_era.tag.ErosionEraTags;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Consumer;


public abstract class ErosionEraBlocks {

    /**
     * @param name 注册名称
     * @param block 注册方块
     * @return 注册方块实例
     */

    protected static Block register(String name, Block block){
        return Registry.register(Registry.BLOCK, new Identifier(ErosionEraMod.ID, name), block);
    }

    /**
     * @param name 注册名称
     * @param block 注册方块
     * @param handlers 方块处理器
     * @return 注册方块实例
     */
    protected static Block register(String name, Block block, BlockHandler... handlers){
        for (BlockHandler handler: handlers) handler.consumer.accept(block);
        return Registry.register(Registry.BLOCK, new Identifier(ErosionEraMod.ID, name), block);
    }

    public static class Settings extends FabricBlockSettings {

        protected Settings(Material material, MaterialColor color) {
            super(material, color);
        }

        protected Settings(FabricBlockSettings settings){
            super(settings);
        }

        public static Settings copyOf(Block block){
            return new Settings(FabricBlockSettings.copyOf(block));
        }

        public static Settings of(Material material){
            return new Settings(FabricBlockSettings.of(material, material.getColor()));
        }

        public static Settings of(Material material, MaterialColor materialColor){
            return new Settings(FabricBlockSettings.of(material, materialColor));
        }

        // 侵蚀设定
        public Settings eroded(){
            return (Settings) this
                    // 只能通过侵蚀类工具破坏（产生交互）
                    .breakByTool(ErosionEraTags.EROSION_TOOL)
                    // 响应标签效果
                    .requiresTool();
        }


    }

    protected enum BlockHandler {
        Translucent(block -> BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getTranslucent()));

        public Consumer<Block> consumer;
        BlockHandler(Consumer<Block> consumer){
            this.consumer = consumer;
        }
    }
}
