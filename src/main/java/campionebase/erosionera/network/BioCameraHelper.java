package campionebase.erosionera.network;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class BioCameraHelper {
    public static final double MAX_PICK_RANGE = 64.0;

    public static BlockHitResult pickBlock(@NotNull BlockGetter level,
                                           @NotNull BlockPos cameraPos,
                                           float yaw, float pitch)
    {
        Vec3 start = cameraPos.getCenter();
        float yawRad = -yaw * Mth.DEG_TO_RAD; // MC 是左撇子
        float pitchRad = -pitch * Mth.DEG_TO_RAD; // MC 还是低头族
        Vec3 lookVec = new Vec3(
                Math.sin(yawRad) * Math.cos(pitchRad),
                Math.sin(pitchRad),
                Math.cos(yawRad) * Math.cos(pitchRad)
        );

        Vec3 end = start.add(lookVec.scale(MAX_PICK_RANGE));
        // 添加偏移量使起点离开摄像机
        ClipContext context = new ClipContext(start.add(lookVec.scale(1.0)), end,
                ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, null);
        return level.clip(context);
    }
}
