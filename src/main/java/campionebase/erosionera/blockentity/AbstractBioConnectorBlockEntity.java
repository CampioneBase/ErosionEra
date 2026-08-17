package campionebase.erosionera.blockentity;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.api.IBioConnector;
import campionebase.erosionera.network.BioNetData;
import campionebase.erosionera.network.BioMachineryService;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeBlockEntity;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = ErosionEra.MODID)
public abstract class AbstractBioConnectorBlockEntity extends BlockEntity implements IBioConnector {
    private static final String TAG_NEIGHBORS_POS = "tag_neighbors";
    @NotNull
    private final Set<BlockPos> neighbors = ConcurrentHashMap.newKeySet();

    public AbstractBioConnectorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public @NotNull Set<BlockPos> getNeighbors(){
        return this.neighbors;
    }

    /** 更新邻居集合 */
    public void updateNeighborPosSet(){
        if (this.level instanceof ServerLevel serverLevel){
            this.neighbors.clear();
            this.neighbors.addAll(BioNetData.get(serverLevel).getNeighbors(this.getBlockPos()));
            this.setChanged();
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putLongArray(TAG_NEIGHBORS_POS, neighbors.stream().mapToLong(BlockPos::asLong).toArray());
        return tag;
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        if (tag.contains(TAG_NEIGHBORS_POS)){
            this.neighbors.clear();
            this.neighbors.addAll(
                    Arrays.stream(tag.getLongArray(TAG_NEIGHBORS_POS))
                            .mapToObj(BlockPos::of)
                            .collect(Collectors.toSet())
            );
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        this.updateNeighborPosSet();
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    /* 由于 setRemoved 会在关闭世界时存档保存之前触发，所以改用 Forge 事件处理
    @Override
    public void setRemoved() {
        super.setRemoved();
        if (this.level instanceof ServerLevel serverLevel){
            // 更新邻居
            Set<BlockPos> neighbors = BioNetData.get(serverLevel).remove(this.getBlockPos());
            neighbors.forEach(neighbor -> {
                if (this.level.getBlockEntity(neighbor) instanceof AbstractBioConnectorBlockEntity connector){
                    connector.updateNeighbors();
                }
            });
        }
    }
    */

    /** 获取导线端点偏移量 */
    @NotNull
    public Vec3 getWirePos(BlockPos neighborPos){
        return this.getBlockPos().getCenter();
    }

    @Override
    public AABB getRenderBoundingBox() {
        // 剔除判断下沉到渲染器
        return INFINITE_EXTENT_AABB;
    }
}
