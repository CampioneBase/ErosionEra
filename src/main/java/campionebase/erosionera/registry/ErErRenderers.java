package campionebase.erosionera.registry;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.client.renderer.EmptyRendererProviders;
import campionebase.erosionera.client.renderer.block.RuneCraftingTableRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        value = Dist.CLIENT,
        bus = Mod.EventBusSubscriber.Bus.MOD,
        modid = ErosionEra.MODID
)
public class ErErRenderers {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event){
        event.registerEntityRenderer(ErErEntities.MAGIC_BULLET.get(), EmptyRendererProviders.ENTITY);

        event.registerBlockEntityRenderer(ErErBlockEntities.RUNE_CRAFTING_TABLE.get(), RuneCraftingTableRenderer::new);
    }
}
