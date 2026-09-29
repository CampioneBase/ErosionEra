package campionebase.erosionera.blockentity;

import campionebase.erosionera.api.BioMachineData;
import campionebase.erosionera.api.IBioMachine;
import campionebase.erosionera.network.BioMachineryNetwork;
import campionebase.erosionera.network.BioMachineryService;
import campionebase.erosionera.network.packet.c2s.BioMachineRenamePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class AbstractBioMachineBlockEntity extends BlockEntity implements IBioMachine {
    @Nullable
    private String customName;

    protected AbstractBioMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public CompoundTag getCustomData() {
        CompoundTag tag = IBioMachine.super.getCustomData();
        if (this.hasLevel()) {
            tag.putBoolean(TAG_LOADED, true);
        }
        return tag;
    }

    public @NotNull Component getDisplayName() {
        return this.customName == null ? Component.literal(this.getDefaultName()) : Component.literal(this.customName);
    }

    @Override
    public void setName(String name) {
        String normalized = name == null ? "" : name.trim();
        String newName = normalized.isEmpty() ? null : normalized;
        if (Objects.equals(this.customName, newName)) return;

        if (this.level == null) return;
        if (this.level.isClientSide){
            // 在客户端，则使用网络包传输到服务端
            BioMachineryNetwork.INSTANCE.sendToServer(
                    new BioMachineRenamePacket(this.getBlockPos(), name)
            );
            return;
        }

        this.customName = newName;
        this.setChanged();
        if (this.level instanceof ServerLevel serverLevel) {
            serverLevel.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
            BioMachineryService.broadcastBioMachineUpdate(serverLevel, BioMachineData.of(this));
        }
    }

    @Override
    public @Nullable String getName() {
        return this.customName ;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        if (this.customName != null) {
            tag.putString(TAG_NAME, this.customName);
        }
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.customName = tag.contains(TAG_NAME, Tag.TAG_STRING) ? tag.getString(TAG_NAME) : null;
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        this.saveAdditional(tag);
        return tag;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
