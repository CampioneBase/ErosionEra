package campionebase.erosionera.blockentity;

import campionebase.erosionera.api.*;
import campionebase.erosionera.block.BioControllerBlock;
import campionebase.erosionera.network.BioMachineryService;
import campionebase.erosionera.registry.ErErBlockEntities;
import campionebase.erosionera.inventory.BioControllerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class BioControllerBlockEntity extends BlockEntity implements MenuProvider, IBioController, SimpleWaterloggedBlock {
    private static final String TAG_TARGET = "TargetUUID";

    @Nullable
    private Player owner; // 服务端数据

    @Nullable
    private Entity target;

    @Nullable
    private UUID targetId;

    public BioControllerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ErErBlockEntities.BIO_CONTROLLER.get(), pos, blockState);
    }

    @Override
    public @Nullable Player getUser() {
        return this.owner;
    }

    @Override
    public @Nullable IBioCore getCore() {
        if (this.level == null) return null;
        if (this.level.getBlockEntity(this.getBlockPos().below()) instanceof IBioCore core) return core;
        return null;
    }

    @Override
    public void onReleased() {
        this.owner = null;
        if (this.level != null && !this.level.isClientSide){
            this.level.setBlock(this.getBlockPos(),
                    this.getBlockState().setValue(BioControllerBlock.OCCUPIED, false),
                    Block.UPDATE_ALL);
            this.setChanged();
        }
    }

    @Override
    public @Nullable Entity getTarget() {
        Entity entity = this.target;
        if (entity != null && entity.isAlive()) return entity;
        if (this.targetId != null && this.level instanceof ServerLevel serverLevel) {
            entity = serverLevel.getEntity(this.targetId);
            if (entity != null && entity.isAlive()) {
                this.target = entity;
                return entity;
            }
        }
        return null;
    }

    @Override
    public void setTarget(@Nullable Entity target) {
        this.target = target;
        this.targetId = target == null ? null : target.getUUID();
        this.setChanged();
        if (this.level instanceof ServerLevel serverLevel) {
            serverLevel.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    public void control(IBioControllable target, Action action) {
        if (this.level instanceof ServerLevel && this.owner instanceof ServerPlayer player) {
            target.onControlledAction(player, action);
        }
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable(this.getBlockState().getBlock().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory inventory, @NotNull Player player) {
        this.owner = player;
        if (this.level == null) return null;
        return new BioControllerMenu(windowId, this.level, this.getBlockPos());
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        if (this.targetId != null) {
            tag.putUUID(TAG_TARGET, this.targetId);
        }
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        if (tag.contains(TAG_TARGET, Tag.TAG_INT_ARRAY)) {
            this.targetId = tag.getUUID(TAG_TARGET);
        }
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
