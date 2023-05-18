package com.lunastic.erosion_era.block.eroded;

import com.lunastic.erosion_era.ErosionEraMod;
import com.lunastic.erosion_era.block.ErosionEraBlocks;
import com.lunastic.erosion_era.tag.ErosionEraTags;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * 侵蚀方块总类
 * @author Lunastic
 */
public class ErodedBlock extends Block {

    public static final ItemGroup GROUP = FabricItemGroupBuilder
            .create(new Identifier(ErosionEraMod.NAMESPACE, "eroded_block"))
            .icon(() -> new ItemStack(ErosionEraBlocks.ERODED_GRASS_BLOCK))
            .build();

    public ErodedBlock(String name, Block.Settings settings) {
        super(settings);
        Registry.register(Registry.BLOCK, new Identifier(ErosionEraMod.NAMESPACE, name), this);
        Registry.register(Registry.ITEM, new Identifier(ErosionEraMod.NAMESPACE, name), new BlockItem(this,
                new FabricItemSettings().group(ErodedBlock.GROUP)
        ));
        // 处理半透明纹理
        BlockRenderLayerMap.INSTANCE.putBlock(this, RenderLayer.getTranslucent());
    }
    /** 侵蚀方块默认设置 */
    public static class Settings extends FabricBlockSettings{
        private Settings(FabricBlockSettings settings) {
            super(settings
                    // 只能通过侵蚀类工具破坏（产生交互）
                    .breakByTool(ErosionEraTags.EROSION_TOOL)
                    // 响应标签效果
                    .requiresTool()
            );
        }

        public static Settings copyOf(Block block){
            return new Settings(FabricBlockSettings.copyOf(block));
        }

        public static Settings of(Material material, MaterialColor materialColor){
            return new Settings(FabricBlockSettings.of(material, materialColor));
        }
    }
}
