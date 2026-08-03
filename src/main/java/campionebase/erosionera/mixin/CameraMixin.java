package campionebase.erosionera.mixin;

import campionebase.erosionera.api.IBioCamera;
import campionebase.erosionera.inventory.BioControllerMenu;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public class CameraMixin {

    @Shadow
    private boolean detached;

    @Inject(method = "isDetached", at = @At("HEAD"), cancellable = true)
    private void cameraDetached(CallbackInfoReturnable<Boolean> cir){
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;
        if (!(player.containerMenu instanceof BioControllerMenu menu)) return;

        IBioCamera cameraPos = menu.getCamera();
        cir.setReturnValue(cameraPos != null || this.detached);
    }
}
