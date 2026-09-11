package campionebase.erosionera.api;

import campionebase.erosionera.registry.BioMachineTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

// 目前的设计 摄像机类 还是无法摆脱方块
public interface IBioCamera extends IBioMachine{

    @Override
    default @NotNull BioMachineType<IBioCamera> getMachineType(){
        return BioMachineTypes.CAMERA.get();
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
