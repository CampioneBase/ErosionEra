package campionebase.erosionera.client.screen;

import campionebase.erosionera.init.ErErAttributes;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber({Dist.CLIENT})
public class ErosionHUD {
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void eventHandler(RenderGuiEvent.Pre event) {
        int w = event.getWindow().getGuiScaledWidth();
        int h = event.getWindow().getGuiScaledHeight();
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            AttributeInstance level = player.getAttribute(ErErAttributes.EROSION_LEVEL.get());
            if (level != null)
                event.getGuiGraphics().drawString(
                        Minecraft.getInstance().font,
                        String.format("Lv. %.0f", level.getBaseValue()),
                        w - 45, h - 25, 0xffcccccc, false);
            AttributeInstance value = player.getAttribute(ErErAttributes.EROSION_VALUE.get());
            if(value != null)
                event.getGuiGraphics().drawString(
                        Minecraft.getInstance().font,
                        String.format("%.0f EE", value.getBaseValue()),
                        w - 45, h - 16, 0xffcccccc, false);
        }
    }

}