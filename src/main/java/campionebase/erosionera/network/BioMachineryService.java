package campionebase.erosionera.network;

import campionebase.erosionera.api.IBioCamera;
import campionebase.erosionera.api.IBioConnector;
import campionebase.erosionera.api.IBioController;
import campionebase.erosionera.api.IBioMachine;
import campionebase.erosionera.blockentity.AbstractBioConnectorBlockEntity;
import campionebase.erosionera.network.packet.BioCameraListPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class BioMachineryService {
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
    /** 通过连接器寻找到相连的 Bio Machine */
    public static @NotNull Set<IBioMachine> findAllConnectedByConnector(@NotNull ServerLevel level, @NotNull BlockPos pos){
        return findAllSurroundConnector(level, pos)
                .stream()
                .flatMap(connector -> BioNetData.get(level).getAllConnectedBlocks(connector.getBlockPos()).stream())
                .map(level::getBlockEntity)
                .filter(blockEntity -> blockEntity instanceof IBioConnector)
                .map(connector -> ((IBioConnector) connector).getMachine())
                .filter(Objects::nonNull)
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
    /** 向节点所在网络内正在使用控制器的玩家广播摄像机列表 */
    public static void broadcastBioCameraList(@NotNull ServerLevel level, @NotNull BlockPos node){
        BioMachineryService
                .findAllConnectedByConnector(level, node)
                .forEach(machine -> {
                    Map<BlockPos, String> cameraOccupations = new HashMap<>();
                    if (!(machine instanceof IBioController controller) || !(controller.getUser() instanceof ServerPlayer serverPlayer)) return;
                    BioMachineryService.findAllConnectedByConnector(level, controller.getBlockPos())
                            .forEach(terminal -> {
                                if (!(terminal instanceof IBioCamera camera)) return;
                                BioCameraManager.CameraOccupation occupation = BioCameraManager.get(level).getCameraOwner(camera.getBlockPos());
                                cameraOccupations.put(camera.getBlockPos(), occupation == null ? null : occupation.getPlayerName());
                            });
                    BioMachineryNetwork.INSTANCE.send(
                            PacketDistributor.PLAYER.with(() -> serverPlayer),
                            new BioCameraListPacket.Response(controller.getBlockPos(), cameraOccupations)
                    );
                });
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
        broadcastBioCameraList(level, b);
    }

    /** 断开 */
    public static void disconnectNodes(@NotNull ServerLevel level,
                                       @NotNull BlockPos a,
                                       @NotNull BlockPos b)
    {
        BioNetData.get(level).disconnect(a, b);
        broadcastBioCameraList(level, b);
        if (!BioNetData.get(level).isTopologicallyConnected(a, b)){
            broadcastBioCameraList(level, a);
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
            broadcastBioCameraList(level, pos);
        });
    }
}

