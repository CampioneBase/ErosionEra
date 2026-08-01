package campionebase.erosionera.api;

import net.minecraft.world.phys.Vec3;

public interface IBioCamera {

    // 根据视角朝向获取摄像机实际位置
    Vec3 getCameraPosition(float yaw, float pitch);
}
