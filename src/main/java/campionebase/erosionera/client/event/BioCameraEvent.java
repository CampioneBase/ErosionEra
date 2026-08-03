package campionebase.erosionera.client.event;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.api.IBioCamera;
import campionebase.erosionera.inventory.BioControllerMenu;
import campionebase.erosionera.mixin.CameraAccessor;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = ErosionEra.MODID,
        value = Dist.CLIENT
)
public class BioCameraEvent {
    @SubscribeEvent
    public static void setCameraAngles(ViewportEvent.ComputeCameraAngles event){
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;
        if (!(player.containerMenu instanceof BioControllerMenu menu)) return;

        IBioCamera bioCamera = menu.getCamera();
        if (bioCamera == null) return;

        Camera camera = event.getCamera();
        float yaw = menu.cameraYaw;
        float pitch = menu.cameraPitch;
        ((CameraAccessor) camera).invokeSetPosition(bioCamera.getCameraPosition(yaw, pitch));
        event.setYaw(yaw);
        event.setPitch(pitch);
    }

    // 取消手臂渲染
    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;
        if (player.containerMenu instanceof BioControllerMenu menu && menu.getCamera() != null) {
            event.setCanceled(true);
        }
    }
}
