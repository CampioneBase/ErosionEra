package campionebase.erosionera.mixin;

import campionebase.erosionera.api.IBioCamera;
import campionebase.erosionera.inventory.BioControllerMenu;
import com.mojang.blaze3d.audio.Listener;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundEngine.class)
public class SoundEngineMixin {

    @Final
    @Shadow
    private Listener listener;

    @Inject(method = "updateSource", at = @At("HEAD"), cancellable = true)
    private void overrideListenerForBioCamera(Camera camera, CallbackInfo ci){
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null) return;

        if (!(player.containerMenu instanceof BioControllerMenu menu)) return;
        IBioCamera bioCamera = menu.getMachine();
        if (bioCamera == null) return;

        Vec3 center = bioCamera.getBlockPos().getCenter();

        float yawRad = (float) Math.toRadians(menu.cameraYaw);
        float pitchRad = (float) Math.toRadians(menu.cameraPitch);

        Vector3f look = new Vector3f(
                -Mth.sin(yawRad) * Mth.cos(pitchRad),
                -Mth.sin(pitchRad),
                Mth.cos(yawRad) * Mth.cos(pitchRad)
        );
        Vector3f up = new Vector3f(
                Mth.sin(yawRad) * Mth.sin(pitchRad),
                Mth.cos(pitchRad),
                -Mth.cos(yawRad) * Mth.sin(pitchRad)
        );

        this.listener.setListenerPosition(center);
        this.listener.setListenerOrientation(look, up);
        ci.cancel();
    }
}
