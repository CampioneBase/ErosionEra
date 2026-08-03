package campionebase.erosionera.network;

import campionebase.erosionera.api.IBioController;
import campionebase.erosionera.api.IBioMachine;
import campionebase.erosionera.blockentity.AbstractBioConnectorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class BioNetHelper {
    /** 寻找所有和此位置方块相连的 Bio Machine */
    public static @NotNull Set<IBioMachine> findAllConnectedFromConnector(@NotNull ServerLevel level, @NotNull BlockPos pos){
        return BioNetData.get(level)
                .getAllConnectedBlocks(pos)
                .stream()
                .map(level::getBlockEntity)
                .filter(blockEntity -> blockEntity instanceof AbstractBioConnectorBlockEntity)
                .map(connector -> ((AbstractBioConnectorBlockEntity) connector).getMachinery())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
    /** 通过连接器寻找到相连的 Bio Machine */
    public static @NotNull Set<IBioMachine> findAllConnectedByConnector(@NotNull ServerLevel level, @NotNull BlockPos pos){
        return findAllSurroundConnector(level, pos)
                .stream()
                .flatMap(connector -> BioNetData.get(level).getAllConnectedBlocks(connector.getBlockPos()).stream())
                .map(level::getBlockEntity)
                .filter(blockEntity -> blockEntity instanceof AbstractBioConnectorBlockEntity)
                .map(connector -> ((AbstractBioConnectorBlockEntity) connector).getMachinery())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
    /** 寻找到周围与之相连的连接器 */
    public static @NotNull Set<AbstractBioConnectorBlockEntity> findAllSurroundConnector(@NotNull ServerLevel level, @NotNull BlockPos pos){
        // 如果的目标本身就是一个连接器（通常是指带功能的连接器），则返回自身
        if (level.getBlockEntity(pos) instanceof AbstractBioConnectorBlockEntity connector) return Set.of(connector);
        // 从邻居方块开始查询
        Set<AbstractBioConnectorBlockEntity> result = new HashSet<>();
        for (Direction direction: Direction.values()) {
            BlockPos neighbor = pos.relative(direction);
            if (level.getBlockEntity(neighbor) instanceof AbstractBioConnectorBlockEntity connector &&
                    connector.getMachinery() != null &&
                    pos.equals(connector.getMachinery().getBlockPos())
            ) result.add(connector);
        }
        return result;
    }

    public static boolean isConnected(@NotNull ServerLevel level, @NotNull BlockPos a, @NotNull BlockPos b){
        Set<AbstractBioConnectorBlockEntity> connectors_a = findAllSurroundConnector(level, a);
        Set<AbstractBioConnectorBlockEntity> connectors_b = findAllSurroundConnector(level, b);
        for (AbstractBioConnectorBlockEntity connector_a: connectors_a) {
            for (AbstractBioConnectorBlockEntity connector_b: connectors_b) {
                if (BioNetData.get(level).isTopologicallyConnected(connector_a.getBlockPos(), connector_b.getBlockPos())) return true;
            }
        }
        return false;
    }
}

