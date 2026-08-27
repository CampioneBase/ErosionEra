package campionebase.erosionera.network;

import campionebase.erosionera.api.IBioCamera;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class BioCameraHelper {
    public static final double MAX_PICK_RANGE = 64.0;

    public static BlockHitResult pickBlock(@NotNull BlockGetter level,
                                           @NotNull IBioCamera camera,
                                           float yaw, float pitch)
    {
        Vec3 start = camera.getCameraPosition(yaw, pitch);
        float yawRad = -yaw * Mth.DEG_TO_RAD; // MC 是左撇子
        float pitchRad = -pitch * Mth.DEG_TO_RAD; // MC 还是低头族
        Vec3 lookVec = new Vec3(
                Math.sin(yawRad) * Math.cos(pitchRad),
                Math.sin(pitchRad),
                Math.cos(yawRad) * Math.cos(pitchRad)
        );

        Vec3 end = start.add(lookVec.scale(MAX_PICK_RANGE));
        return level.clip(new ClipContext(
                // 添加偏移量使起点离开摄像机
                start.add(lookVec.scale(1.0)),
                end,
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                null
        ));
    }

    @Nullable
    public static EntityHitResult pickEntity(@NotNull Level level,
                                             @NotNull IBioCamera camera,
                                             float yaw, float pitch)
    {
        Vec3 start = camera.getCameraPosition(yaw, pitch);
        float yawRad = -yaw * Mth.DEG_TO_RAD; // MC 是左撇子
        float pitchRad = -pitch * Mth.DEG_TO_RAD; // MC 还是低头族
        Vec3 lookVec = new Vec3(
                Math.sin(yawRad) * Math.cos(pitchRad),
                Math.sin(pitchRad),
                Math.cos(yawRad) * Math.cos(pitchRad)
        );
        Vec3 end = start.add(lookVec.scale(MAX_PICK_RANGE));

        AABB searchBox = new AABB(start, end).inflate(2.0);
        List<Entity> entities = level.getEntities((Entity) null, searchBox, e ->
                e.isAlive() && e.isPickable() && !e.isSpectator()
        );

        EntityHitResult result = null;
        double distanceSqr = MAX_PICK_RANGE * MAX_PICK_RANGE;
        for (Entity entity : entities) {
            // 获取射线命中位置
            Vec3 hitPosition = entity.getBoundingBox().clip(start, end).orElse(null);
            if (hitPosition == null) continue;

            double distToStart = start.distanceToSqr(hitPosition);
            if (distToStart >= distanceSqr) continue;

            distanceSqr = distToStart;
            result = new EntityHitResult(entity, hitPosition);
        }
        return result;
    }

    @NotNull
    public static HitResult pick(@NotNull Level level, @NotNull IBioCamera camera, float yaw, float pitch){
        final double offset = 1.0;

        Vec3 start = camera.getCameraPosition(yaw, pitch);
        float yawRad = -yaw * Mth.DEG_TO_RAD; // MC 是左撇子
        float pitchRad = -pitch * Mth.DEG_TO_RAD; // MC 还是低头族
        Vec3 lookVec = new Vec3(
                Math.sin(yawRad) * Math.cos(pitchRad),
                Math.sin(pitchRad),
                Math.cos(yawRad) * Math.cos(pitchRad)
        );
        Vec3 end = start.add(lookVec.scale(MAX_PICK_RANGE));
        BlockHitResult blockHit = level.clip(new ClipContext(
                start.add(lookVec.scale(offset)),
                end,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                null
        ));
        double distanceSqr = blockHit.getType() == HitResult.Type.MISS ?
                MAX_PICK_RANGE * MAX_PICK_RANGE :
                start.distanceToSqr(blockHit.getLocation());
        HitResult result = blockHit;

        AABB searchBox = new AABB(start, end).inflate(2.0);
        List<Entity> entities = level.getEntities((Entity) null, searchBox, e ->
                e.isAlive() && e.isPickable() && !e.isSpectator()
        );

        for (Entity entity : entities) {
            // 获取射线命中位置
            Vec3 hitPosition = entity.getBoundingBox().clip(start, end).orElse(null);
            if (hitPosition == null) continue;

            double distToStart = start.distanceToSqr(hitPosition);
            if (distToStart >= distanceSqr) continue;

            BlockHitResult checkBlock = level.clip(new ClipContext(
                    start.add(lookVec.scale(offset)), hitPosition,
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    null
            ));

            if (checkBlock.getType() != HitResult.Type.MISS) {
                if (start.distanceToSqr(checkBlock.getLocation()) <= distToStart) {
                    continue;
                }
            }

            distanceSqr = distToStart;
            result = new EntityHitResult(entity, hitPosition);
        }

        return result;
    }
}
