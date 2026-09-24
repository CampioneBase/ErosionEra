package campionebase.erosionera.api.render;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface BioMachineRenderable {
    void render(GuiGraphics graphics, int x, int y, int width, int height, boolean selected, float partialTick);

    default int preferredHeight(){ return 28; }
}
