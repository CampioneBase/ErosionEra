package campionebase.erosionera.network;

import campionebase.erosionera.api.IBioMachine;
import campionebase.erosionera.block.entity.AbstractBioConnectorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class BioNetHelper {
    /** 寻找所有和此位置方块相连的 Bio Machine */
    public static @NotNull Set<IBioMachine> findAllConnectedBioMachines(@NotNull ServerLevel level, @NotNull BlockPos pos){
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
        Set<AbstractBioConnectorBlockEntity> result = new HashSet<>();
        for (Direction direction: Direction.values()) {
            BlockPos neighbor = pos.relative(direction);
            BlockState blockState = level.getBlockState(neighbor);
            // 同向检测，既连接器与该方块相连
            if (!blockState.hasProperty(BlockStateProperties.FACING))
                continue;
            Direction facing = level.getBlockState(neighbor).getValue(BlockStateProperties.FACING);
            if (facing != direction)
                continue;
            if (level.getBlockEntity(neighbor) instanceof AbstractBioConnectorBlockEntity connector)
                result.add(connector);
        }
        return result;
    }
}

