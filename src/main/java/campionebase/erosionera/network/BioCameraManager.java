package campionebase.erosionera.network;

import campionebase.erosionera.api.IBioCamera;
import campionebase.erosionera.api.IBioController;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BioCameraManager {
    public static final Logger LOGGER = LogManager.getLogger(BioCameraManager.class);

    public static final Map<ServerLevel, BioCameraManager> INSTANCES = new ConcurrentHashMap<>();
    public static BioCameraManager get(ServerLevel level){
        return INSTANCES.computeIfAbsent(level, BioCameraManager::new);
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
            // 占用的玩家存在，则直接返回
            if (owner != null) return owner;
        }
        this.occupyCamera(camera, player);
        return player;
    }

    private void occupyCamera(BlockPos camera, Player player){
        LOGGER.debug("Player: {} occupy bio-camera[{}]", player.getName().getString(), camera.toShortString());
        this.cameraOccupations.put(camera, new CameraOccupation(player));
    }

    /** 释放摄像机 */
    public void releaseCamera(BlockPos camera){
        if (camera == null) return;
        LOGGER.debug("bio-camera[{}] released", camera.toShortString());
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
