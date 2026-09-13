package campionebase.erosionera.network;

import campionebase.erosionera.api.*;
import campionebase.erosionera.blockentity.AbstractBioConnectorBlockEntity;
import campionebase.erosionera.network.packet.rr.BioMachineListPacket;
import campionebase.erosionera.network.packet.s2c.BioMachineUpdatePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 活体机械服务层
 */
public class BioMachineryService {
    //region ================================== 验证 =======================================

    /**
     * 摄像机控制网络包验证验证
     * @param sender 网络包发送人
     * @param cameraPos 目标摄像机
     * @param controllerPos 状态控制器
     * @param failureText 失败信息
     * @return 是否有效
     */
    public static boolean authenticateCameraController(ServerPlayer sender, BlockPos cameraPos, BlockPos controllerPos, String failureText){
        ServerLevel level = sender.serverLevel();
        if (!(level.getBlockEntity(cameraPos) instanceof IBioCamera camera)) {
            BioMachineryNetwork.LOGGER.warn(
                    "{}: bio-camera[{}] invalid",
                    failureText, cameraPos.toShortString()
            );
            return false;
        }

        if (!(level.getBlockEntity(controllerPos) instanceof IBioController controller)) {
            BioMachineryNetwork.LOGGER.warn(
                    "{}: bio-controller[{}] invalid",
                    failureText, controllerPos.toShortString()
            );
            return false;
        }

        Player user = controller.getUser();
        if (user == null || !user.getUUID().equals(sender.getUUID())){
            BioMachineryNetwork.LOGGER.warn(
                    "{}: occupier[{}] invalid",
                    failureText, sender.getName()
            );
            return false;
        }

        IBioCore core = controller.getCore();
        if (core == null) {
            BioMachineryNetwork.LOGGER.warn(
                    "{}: bio-controller[{}] missing core",
                    failureText, controllerPos.toShortString()
            );
            return false;
        }

        if (!isConnected(level, cameraPos, core.getBlockPos())){
            BioMachineryNetwork.LOGGER.warn(
                    "{}: bio-camera[{}] not connect to bio-core[{}] ",
                    failureText, cameraPos.toShortString(), core.getBlockPos().toShortString()
            );
            return false;
        }
        return true;
    }

    //endregion
    //region ================================== 查询 =======================================

    /** 寻找所有和此位置方块相连的 Bio Machine */
    public static @NotNull Set<IBioMachine> findAllConnectedFromConnector(@NotNull ServerLevel level, @NotNull BlockPos pos){
        return BioNetData.get(level)
                .getAllConnectedBlocks(pos)
                .stream()
                .map(level::getBlockEntity)
                .filter(blockEntity -> blockEntity instanceof IBioConnector)
                .map(connector -> ((IBioConnector) connector).getMachine())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /** 通过连接器寻找到相连的 Bio Machine （不包含自身） */
    public static @NotNull Set<IBioMachine> findAllConnectedByConnector(@NotNull ServerLevel level, @NotNull BlockPos pos){
        return findAllConnectedByConnector(level, pos, false);
    }
    /** 通过连接器寻找到相连的 Bio Machine */
    public static @NotNull Set<IBioMachine> findAllConnectedByConnector(@NotNull ServerLevel level, @NotNull BlockPos pos, boolean containSelf){
        return findAllSurroundConnector(level, pos)
                .stream()
                .flatMap(connector -> BioNetData.get(level).getAllConnectedBlocks(connector.getBlockPos()).stream())
                .map(level::getBlockEntity)
                .filter(blockEntity -> blockEntity instanceof IBioConnector)
                .map(connector -> ((IBioConnector) connector).getMachine())
                .filter(Objects::nonNull)
                // 是否剔除自身
                .filter(machine -> containSelf || !machine.getBlockPos().equals(pos))
                .collect(Collectors.toSet());
    }
    /** 寻找到周围与之相连的连接器 */
    public static @NotNull Set<IBioConnector> findAllSurroundConnector(@NotNull ServerLevel level, @NotNull BlockPos pos){
        // 如果的目标本身就是一个连接器（通常是指带功能的连接器），则返回自身
        if (level.getBlockEntity(pos) instanceof IBioConnector connector) return Set.of(connector);
        // 从邻居方块开始查询
        Set<IBioConnector> result = new HashSet<>();
        for (Direction direction: Direction.values()) {
            BlockPos neighbor = pos.relative(direction);
            if (level.getBlockEntity(neighbor) instanceof IBioConnector connector &&
                    connector.getMachine() != null &&
                    pos.equals(connector.getMachine().getBlockPos())
            ) result.add(connector);
        }
        return result;
    }

    //endregion
    //region ================================== 广播 =======================================

    /** 向节点所在网络内正在使用控制器的玩家广播全量列表更新 */
    public static void broadcastBioMachineListUpdate(@NotNull ServerLevel level, @NotNull BlockPos node){
        BioMachineryService
                .findAllConnectedByConnector(level, node)
                .forEach(machine -> {
                    if (!(machine instanceof IBioCore core)) return;
                    if (core.getController() == null) return;
                    if (!(core.getController().getUser() instanceof ServerPlayer serverPlayer)) return;
                    Set<BioMachineData> dataSet = findAllConnectedByConnector(level, core.getBlockPos())
                            .stream()
                            .map(BioMachineData::of)
                            .collect(Collectors.toSet());

                    BioMachineryNetwork.INSTANCE.send(
                            PacketDistributor.PLAYER.with(() -> serverPlayer),
                            new BioMachineListPacket.Response(core.getBlockPos(), dataSet)
                    );
                });
    }

    /** 向节点所在网络内正在使用控制器的玩家广播更新信息 */
    public static void broadcastBioMachineUpdate(
            @NotNull ServerLevel level,
            @NotNull BioMachineData data
    ){
        BlockPos pos = data.pos();
        findAllConnectedByConnector(level, pos).forEach(machine -> {
            if (!(machine instanceof IBioCore core)) return;
            if (core.getController() == null) return;
            if (!(core.getController().getUser() instanceof ServerPlayer serverPlayer)) return;

            BioMachineryNetwork.INSTANCE.send(
                    PacketDistributor.PLAYER.with(() -> serverPlayer),
                    new BioMachineUpdatePacket(data)
            );
        });
    }

    //endregion
    //region ================================== Bio Camera =======================================

    public static void releaseCameraIfOwned(ServerLevel level, BlockPos cameraPos, Player sender){
        if (cameraPos == null) return;
        BioCameraManager.CameraOccupation occupation = BioCameraManager.get(level).getCameraOwner(cameraPos);
        if (occupation == null) return;
        if (!sender.getUUID().equals(occupation.getPlayerUUID())) {
            BioMachineryNetwork.LOGGER.warn(
                    "Preventing {} from releasing camera[{}]: sender is not occupier",
                    sender.getName(), cameraPos.toShortString()
            );
            return;
        }
        BioCameraManager.get(level).releaseCamera(cameraPos);
        if (level.getBlockEntity(cameraPos) instanceof IBioCamera oldCamera) {
            BioMachineryService.broadcastBioMachineUpdate(level, BioMachineData.of(oldCamera));
        }
    }

    //endregion
    //region ================================== Bio Net =======================================

    /** 检测两点是否连通 */
    public static boolean isConnected(@NotNull ServerLevel level, @NotNull BlockPos a, @NotNull BlockPos b){
        Set<IBioConnector> connectors_a = findAllSurroundConnector(level, a);
        Set<IBioConnector> connectors_b = findAllSurroundConnector(level, b);
        for (IBioConnector connector_a: connectors_a) {
            for (IBioConnector connector_b: connectors_b) {
                if (BioNetData.get(level).isTopologicallyConnected(connector_a.getBlockPos(), connector_b.getBlockPos())) return true;
            }
        }
        return false;
    }

    /** 改变两个节点间连接状态 */
    public static boolean changeConnection(@NotNull ServerLevel level,
                                           @NotNull AbstractBioConnectorBlockEntity source,
                                           @NotNull AbstractBioConnectorBlockEntity target)
    {
        boolean isConnected = false;
        BlockPos a = source.getBlockPos();
        BlockPos b = target.getBlockPos();
        BioNetData data = BioNetData.get(level);
        if (data.isDirectlyConnected(a, b)){
            disconnectNodes(level, source.getBlockPos(), target.getBlockPos());
        } else {
            connectNodes(level, source.getBlockPos(), target.getBlockPos());
            isConnected = true;
        }
        source.updateNeighborPosSet();
        target.updateNeighborPosSet();
        return isConnected;
    }

    /** 连接 */
    public static void connectNodes(@NotNull ServerLevel level,
                                    @NotNull BlockPos a,
                                    @NotNull BlockPos b)
    {
        BioNetData.get(level).connect(a, b);
        broadcastBioMachineListUpdate(level, b);
    }

    /** 断开 */
    public static void disconnectNodes(@NotNull ServerLevel level,
                                       @NotNull BlockPos a,
                                       @NotNull BlockPos b)
    {
        BioNetData.get(level).disconnect(a, b);
        broadcastBioMachineListUpdate(level, b);
        if (!BioNetData.get(level).isTopologicallyConnected(a, b)){
            broadcastBioMachineListUpdate(level, a);
        }
    }
    /** 移除网络节点 */
    public static void removeNode(@NotNull ServerLevel level, @NotNull IBioConnector connector){
        // 根据邻居节点更新列表
        removeNode(level, connector.getBlockPos());
    }
    /** 移除 */
    public static void removeNode(@NotNull ServerLevel level, @NotNull BlockPos node){
        BioNetData.get(level).remove(node).forEach(pos -> {
            if (level.getBlockEntity(pos) instanceof AbstractBioConnectorBlockEntity neighbor){
                neighbor.updateNeighborPosSet();
            }
            broadcastBioMachineListUpdate(level, pos);
        });
    }

    //endregion
}

