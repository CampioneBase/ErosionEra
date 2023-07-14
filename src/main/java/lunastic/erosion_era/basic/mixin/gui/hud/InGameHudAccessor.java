package lunastic.erosion_era.basic.mixin.gui.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(InGameHud.class)
public interface InGameHudAccessor {
    @Accessor
    MinecraftClient getClient();

    @Accessor
    int getScaledHeight();

    @Accessor
    int getScaledWidth();
}
