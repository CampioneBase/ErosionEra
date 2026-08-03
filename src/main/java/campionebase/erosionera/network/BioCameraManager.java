package campionebase.erosionera.network;

import campionebase.erosionera.api.IBioCamera;
import campionebase.erosionera.api.IBioController;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BioCameraManager {
    public static final Map<ServerLevel, BioCameraManager> INSTANCES = new ConcurrentHashMap<>();
    public static BioCameraManager get(ServerLevel level){
        return INSTANCES.getOrDefault(level, new BioCameraManager(level));
    }
    private final ServerLevel level;
    /** 摄像机 -> 摄像机占用情况 */
    private final Map<BlockPos, CameraOccupation> cameraOccupations = new ConcurrentHashMap<>();

    public BioCameraManager(ServerLevel level) {
        this.level = level;
    }

    /** 尝试占用摄像机 */
    @NotNull
    public Player tryOccupyCamera(@NotNull BlockPos camera,
                                  @NotNull Player player){
        if (this.cameraOccupations.containsKey(camera)) {
            UUID ownerId = this.cameraOccupations.get(camera).getPlayerUUID();
            Player owner = this.level.getPlayerByUUID(ownerId);
            // 玩家不存在
            if (owner != null) return owner;
        }
        this.occupyCamera(camera, player);
        return player;
    }

    private void occupyCamera(BlockPos camera, Player player){
        this.cameraOccupations.put(camera, new CameraOccupation(player));
    }

    /** 释放摄像机 */
    public void releaseCamera(BlockPos camera){
        this.cameraOccupations.remove(camera);
    }

    public boolean isCameraOccupied(BlockPos camera){
        return this.cameraOccupations.containsKey(camera);
    }

    @Nullable
    public CameraOccupation getCameraOwner(BlockPos camera){
        return this.cameraOccupations.get(camera);
    }
}
