package campionebase.erosionera.api;

import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

// 目前的设计 摄像机类 还是无法摆脱
public interface IBioCamera extends IBioMachine{

    @Override
    default boolean isCore(){
        return false;
    }

    default float getMaxPitch() {
        return 90.0f;
    }

    default float getMinPitch(){
        return -90.0f;
    }

    default float getDefaultPitch(){
        return 0.0f;
    }

    default float getDefaultYaw(){
        return 0.0f;
    }

    // 根据视角朝向获取摄像机实际位置
    Vec3 getCameraPosition(float yaw, float pitch);

    String getName();


}
