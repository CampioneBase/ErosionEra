package campionebase.erosionera.network;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bio Net 数据存储类，负责管理游戏中由方块节点及其连接构成的图结构
 * <p>
 * 该数据以 {@link ServerLevel} 为单位持久化，通过 {@link #get(ServerLevel)} 获取当前维度的实例
 * <p>
 * 当图结构发生变更时，会调用 {@link #rebuildCaches()} 重新分配 UUID
 */
public class BioNetData extends SavedData {
    public static final Logger LOGGER = LogManager.getLogger(BioNetData.class);
    /**
     * 获取指定维度的 Bio Net 数据实例
     * <p>
     * 若该维度尚未存在对应的数据文件，则会创建一个新的空实例并自动保存
     *
     * @param level 服务端世界维度
     * @return 当前维度的 Bio Net 数据对象
     */
    public static BioNetData get(ServerLevel level){
        return level.getDataStorage().computeIfAbsent(
                BioNetData::load, BioNetData::new,
                level.dimension().location().toDebugFileName() + "_bio_net"
        );
    }

    /** 邻接表: 节点坐标 → 其相邻节点的集合 */
    private final Map<BlockPos, Set<BlockPos>> graph = new ConcurrentHashMap<>();
    private static final String TAG_GRAPH = "graph";

    /**
     * 节点到所属网络 UUID 的映射
     * <p>
     * 用于快速定位节点所在的网络，由 {@link #rebuildCaches()} 重新计算
     */
    private final Map<BlockPos, UUID> node2Net = new ConcurrentHashMap<>();

    /**
     * 网络 UUID 到其包含的所有节点集合的缓存
     * <p>
     * 该缓存由 {@link #rebuildCaches()} 构建，用于快速询网络下的所有节点
     */
    private final Map<UUID, Set<BlockPos>> netNodesCache = new ConcurrentHashMap<>();

    /**
     * 将当前图数据序列化到 NBT 标签中
     * <p>
     * 该方法会将 {@link #graph} 中的每个节点及其邻居列表转换为长整型数组并保存
     */
    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        // 保存 graph
        CompoundTag graphTag = new CompoundTag();
        for (Map.Entry<BlockPos, Set<BlockPos>> entry : this.graph.entrySet()) {
            String key = Long.toString(entry.getKey().asLong());
            long[] neighbors = entry.getValue().stream()
                    .mapToLong(BlockPos::asLong)
                    .toArray();
            graphTag.put(key, new LongArrayTag(neighbors));
        }
        tag.put(TAG_GRAPH, graphTag);

        this.setDirty();
        return tag;
    }
    /**
     * 加载图数据并重建缓存（调用 {@link #rebuildCaches()}）
     */
    private static @NotNull BioNetData load(@NotNull CompoundTag tag) {
        BioNetData data = new BioNetData();

        // 加载 graph
        CompoundTag graphTag = tag.getCompound(TAG_GRAPH);
        for (String key : graphTag.getAllKeys()) {
            long posLong = Long.parseLong(key);
            BlockPos pos = BlockPos.of(posLong);

            LongArrayTag neighborTag = (LongArrayTag) graphTag.get(key);
            if (neighborTag == null) continue;
            Set<BlockPos> neighbors = data.graph.computeIfAbsent(pos, k -> ConcurrentHashMap.newKeySet());
            for (long l : neighborTag.getAsLongArray()) {
                BlockPos neighbor = BlockPos.of(l);
                neighbors.add(neighbor);
                // 双向添加
                data.graph.computeIfAbsent(neighbor, k -> ConcurrentHashMap.newKeySet()).add(pos);
            }
        }

        // 重建缓存
        data.rebuildCaches();
        return data;
    }

    /**
     * 全量遍历当前图，重建 {@link #node2Net} 和 {@link #netNodesCache}
     * <p>
     * 该方法通过 BFS 找出图中所有连通分量，并为每个分量分配一个新的随机 UUID
     * <p>
     * <b>注意：</b>此操作会清空现有缓存并完全重新计算
     */
    private void rebuildCaches() {
        this.netNodesCache.clear();
        this.node2Net.clear();
        // 遍历图中所有节点，对每个未访问的连通分量分配一个 UUID
        Set<BlockPos> visited = new HashSet<>();
        for (BlockPos start : this.graph.keySet()) {
            if (visited.contains(start)) continue;
            UUID netId = UUID.randomUUID();
            Set<BlockPos> netNodes = ConcurrentHashMap.newKeySet();
            Queue<BlockPos> queue = new ArrayDeque<>();
            queue.add(start);
            visited.add(start);

            while (!queue.isEmpty()) {
                // 网络 ID 跟随邻边关系扩散
                BlockPos node = queue.poll();
                netNodes.add(node);
                this.node2Net.put(node, netId);
                for (BlockPos neighbor : this.graph.getOrDefault(node, Collections.emptySet())) {
                    if (visited.contains(neighbor)) continue;
                    queue.add(neighbor);
                    visited.add(neighbor);
                }
            }
            this.netNodesCache.put(netId, netNodes);
        }
    }

    /**
     * 获取指定节点所属网络的 UUID
     *
     * @param pos 需要查询的方块坐标
     * @return 所在网络的 UUID，若不属于任何网络则返回 {@code null}
     * @deprecated 网络 ID 是动态生成的，不应在外部持久化或依赖其稳定性 <p>
     *      使用 {@link #getAllConnectedBlocks(BlockPos)} 获取连通节点集合
     */
    @Deprecated
    public @Nullable UUID getNetIdByNode(@NotNull BlockPos pos){
        return this.node2Net.get(pos);
    }

    /**
     * 根据网络 UUID 获取该网络包含的所有节点集合。
     *
     * @param netId 网络 ID
     * @return 属于该网络的节点集合（不可修改），若网络不存在则返回空集合
     * @deprecated 由于网络 ID 是动态的，节点所属网络可能在图变更后发生变化<p>
     *       使用 {@link #getAllConnectedBlocks(BlockPos)} 获取连通节点集合
     */
    @Deprecated
    public @NotNull Set<BlockPos> getNetById(@NotNull UUID netId){
        return this.netNodesCache.getOrDefault(netId, Collections.emptySet());
    }

    /**
     * 获取与指定节点连通的所有方块坐标（包含自身）
     * <p>
     * 该方法利用缓存快速返回所在连通分量的节点集合
     *
     * @param pos 起始节点坐标
     * @return 该连通分量中的所有节点集合，若节点不存在则返回空集合
     */
    public @NotNull Set<BlockPos> getAllConnectedBlocks(@NotNull BlockPos pos){
        UUID netId = this.node2Net.get(pos);
        if (netId == null) return Collections.emptySet();
        return Set.copyOf(this.netNodesCache.getOrDefault(netId, Collections.emptySet()));
    }

    /**
     * 获取与指定节点相邻的所有方块坐标
     * <p>
     * 该方法利用缓存快速返回相邻节点集合
     *
     * @param pos 节点坐标
     * @return 相邻节点集合，若没有相邻节点则返回空集合
     */
    public @NotNull Set<BlockPos> getNeighbors(@NotNull BlockPos pos){
        Set<BlockPos> neighbors = this.graph.get(pos);
        if (neighbors == null) return Collections.emptySet();
        return Set.copyOf(neighbors);
    }

    /**
     * 检查两个节点之间是否存在直接连接（无向边）
     * <p>
     * 该方法仅判断两个坐标是否在图中作为相邻节点直接相连，不会进行连通性传递（即不检测间接路径）
     * 若其中一个节点不在图中，则返回 {@code false}
     *
     * @param a 第一个节点坐标
     * @param b 第二个节点坐标
     * @return 如果 {@code a} 和 {@code b} 之间存在直接的边，则返回 {@code true}；否则返回 {@code false}
     */
    public boolean isConnected(@NotNull BlockPos a, @NotNull BlockPos b){
        return (this.graph.containsKey(a) && this.graph.get(a).contains(b)) ||
                (this.graph.containsKey(b) && this.graph.get(b).contains(a));
    }

    /**
     * 在两个节点之间建立连接（添加无向边）
     * <p>
     * 若任一节点尚未存在于图中，则会自动创建该节点
     * <p>
     * 操作完成后会重建缓存（调用 {@link #rebuildCaches()}）并标记数据为脏
     *
     * @param a 第一个节点坐标
     * @param b 第二个节点坐标
     */
    public void connect(@NotNull BlockPos a, @NotNull BlockPos b){
        // 新建边，首次进行连接也会新建节点
        this.graph.computeIfAbsent(a, k -> ConcurrentHashMap.newKeySet()).add(b);
        this.graph.computeIfAbsent(b, k -> ConcurrentHashMap.newKeySet()).add(a);
        this.rebuildCaches();
        /* 不需要精细操作，直接刷新缓存哒 ~ ↑
        UUID netA = this.getNetIdByNode(a);
        UUID netB = this.getNetIdByNode(b);

        if (netA == null){
            if (netB == null){
                // 两个节点都没有所属网络，创建并将 A B 加入新网络
                UUID netId = UUID.randomUUID();
                Set<BlockPos> set = ConcurrentHashMap.newKeySet();
                set.add(a);
                set.add(b);
                this.netNodesCache.put(netId, set);
            }
            else {
                // 将 A 加入 B网络
                this.getNetById(netB).add(a);
            }
        }
        else if (netB == null){
            // 将 B 加入 A网络
            this.getNetById(netA).add(b);
        }
        else if (!netA.equals(netB)) {
            // 两个节点属于不同网络时，则将其中一个网络的所有节点移动到另一个网络里
            Set<BlockPos> setA = this.getNetById(netA);
            Set<BlockPos> setB = this.getNetById(netB);
            setB.forEach(pos -> {
                this.node2Net.put(pos, netA);
                setA.add(pos);
            });
            this.netNodesCache.remove(netB); // 即时清理，不然就会有一坨空集
        }
        */
        this.setDirty();
        LOGGER.debug("Connect nodes from [{}] to [{}]", a.toShortString(), b.toShortString());
    }

    /**
     * 断开两个节点之间的连接（移除无向边）
     * <p>
     * 操作完成后会重建缓存（调用 {@link #rebuildCaches()}）并标记数据为脏
     *
     * @param a 第一个节点坐标
     * @param b 第二个节点坐标
     */
    public void disconnect(@NotNull BlockPos a, @NotNull BlockPos b){
        Set<BlockPos> setA = this.graph.get(a);
        Set<BlockPos> setB = this.graph.get(b);
        if (setA != null) setA.remove(b);
        if (setB != null) setB.remove(a);
        // 不清理孤立点，直接刷新缓存
        this.rebuildCaches();
        this.setDirty();
        LOGGER.debug("Disconnect nodes from [{}] to [{}]", a.toShortString(), b.toShortString());
    }

    /**
     * 从图中移除指定节点及其所有关联边。
     * <p>
     * 若该节点不存在，则不做任何操作。移除后，其所有邻居的邻接表中也会删除该节点。
     * <p>
     * 操作完成后会重建缓存（调用 {@link #rebuildCaches()}）并标记数据为脏
     *
     * @param pos 要移除的节点坐标
     * @return 返回该节点相邻的节点集合
     */
    public @NotNull Set<BlockPos> remove(@NotNull BlockPos pos){
        Set<BlockPos> neighbors = this.graph.remove(pos);
        if (neighbors == null) return Collections.emptySet();
        neighbors.forEach(neighbor -> {
            this.graph.getOrDefault(neighbor, Collections.emptySet()).remove(pos);
        });
        // 不局部调整，直接刷新缓存
        this.rebuildCaches();
        this.setDirty();
        LOGGER.debug("Remove node[{}]", pos.toShortString());
        return neighbors;
    }
}
