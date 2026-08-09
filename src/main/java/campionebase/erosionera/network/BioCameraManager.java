package campionebase.erosionera.network;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.api.IBioCamera;
import campionebase.erosionera.api.IBioController;
import campionebase.erosionera.network.packet.BioCameraListPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = ErosionEra.MODID)
public class BioCameraManager {
    public static final Logger LOGGER = LogManager.getLogger(BioCameraManager.class);
    /** 服务端检测间隔 */
    public static final int CHECK_TICK_INTERVAL = 20;
    /** 客户端更新间隔 */
    public static final int UPDATE_TICK_INTERVAL = 60;
    /** 服务端保留最大时间 */
    public static final int RETAIN_MAX_TICK = 200;
    // 维度隔离单例
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
        this.cameraOccupations.put(camera, new CameraOccupation(player, this.tickCount));
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

    /** 由占用者刷新摄像机健康状态与视角方向 */
    public boolean tryRenewCamera(BlockPos camera, UUID ownerId, float yaw, float pitch){
        CameraOccupation occupation = this.cameraOccupations.get(camera);
        if (occupation == null || !occupation.getPlayerUUID().equals(ownerId)) return false;
        occupation.yaw = yaw;
        occupation.pitch = pitch;
        occupation.timestamp = this.tickCount;
        LOGGER.trace("[Health] Occupation renew. Pos:{} UserId:{}", camera.toShortString(), ownerId.toString());
        return true;
    }

    /** 在占用摄像机的视角半球上生成方向指示粒子 */
    public void spawnDirectionParticles(){
        this.cameraOccupations.forEach((pos, occupation) -> {
            if (!(this.level.getBlockEntity(pos) instanceof IBioCamera)) return;
            float yaw = occupation.yaw * Mth.DEG_TO_RAD;
            float pitch = occupation.pitch * Mth.DEG_TO_RAD;
            double dx = -Math.sin(yaw) * Math.cos(pitch);
            double dy = -Math.sin(pitch);
            double dz = Math.cos(yaw) * Math.cos(pitch);
            Vec3 center = pos.getCenter();
            this.level.sendParticles(ParticleTypes.END_ROD,
                    center.x + dx * 0.5,
                    center.y + dy * 0.5,
                    center.z + dz * 0.5,
                    1, 0.0D, 0.0D, 0.0D, 0.0D);
        });
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event){
        if (event.getEntity() instanceof ServerPlayer player) {
            ServerLevel level = player.serverLevel();
            get(level).cameraOccupations.forEach((pos, cameraOccupation) -> {
                if (player.getUUID().equals(cameraOccupation.playerUUID)){
                    get(level).releaseCamera(pos);
                    LOGGER.info("[Health] Release camera[{}]: Disconnected", pos.toShortString());
                }
            });
        }
    }

    @SubscribeEvent
    public static void OnLevelTick(TickEvent.LevelTickEvent event){
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.level instanceof ServerLevel serverLevel)) return;
        get(serverLevel).tick();
    }

    private int tickCount = 0;
    private void tick(){
        this.tickCount ++;
        // 检测存活状态
        if (this.tickCount % CHECK_TICK_INTERVAL == 0) {
            this.cameraOccupations.forEach((pos, cameraOccupation) -> {
                int tick = this.tickCount - cameraOccupation.timestamp;
                if (tick > RETAIN_MAX_TICK) {
                    this.releaseCamera(pos);
                    LOGGER.warn("[Health] Occupation timeout and released. Pos:{} User:{}",
                            pos.toShortString(), cameraOccupation.playerName);
                }
            });
        }
    }

    public static class CameraOccupation {
        private final UUID playerUUID;
        private final String playerName;
        public float yaw;
        public float pitch;
        private int timestamp;

        public CameraOccupation(Player player, int tick){
            this.playerName = player.getName().getString();
            this.playerUUID = player.getUUID();
            this.yaw = 0f;
            this.pitch = 0f;
            this.timestamp = tick;
        }

        public UUID getPlayerUUID() {
            return this.playerUUID;
        }

        public String getPlayerName() {
            return this.playerName;
        }
    }

    @Override
    public String toString() {
        return "BioCameraManager{" + this.level.toString() + ", size:" + this.cameraOccupations.size() + '}';
    }
}
