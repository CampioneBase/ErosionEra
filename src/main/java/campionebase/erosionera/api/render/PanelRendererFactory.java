package campionebase.erosionera.api.render;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
@FunctionalInterface
public interface PanelRendererFactory {
    @NotNull BioMachineRenderable create(@NotNull CompoundTag customData);
}
