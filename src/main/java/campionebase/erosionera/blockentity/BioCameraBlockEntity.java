package campionebase.erosionera.blockentity;

import campionebase.erosionera.api.IBioCamera;
import campionebase.erosionera.api.IBioMachine;
import campionebase.erosionera.api.BioMachineData;
import campionebase.erosionera.block.BioCameraBlock;
import campionebase.erosionera.network.BioCameraManager;
import campionebase.erosionera.network.BioMachineryService;
import campionebase.erosionera.registry.ErErBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BioCameraBlockEntity extends AbstractBioConnectorBlockEntity implements IBioCamera {
    public static final String TAG_NAME = "CustomName";
    public static final String TAG_OCCUPIER = "Occupier";
    @Nullable
    private String customName;

    public BioCameraBlockEntity(BlockPos pos, BlockState blockState) {
        super(ErErBlockEntities.BIO_CAMERA.get(), pos, blockState);
    }

    @Override
    public IBioMachine getMachine() {
        return this;
    }

    @Override
    public CompoundTag getCustomData() {
        CompoundTag tag = IBioCamera.super.getCustomData();
        if (this.level instanceof ServerLevel serverLevel){
            BioCameraManager.CameraOccupation occupation = BioCameraManager.get(serverLevel).getCameraOwner(this.getBlockPos());
            if (occupation != null) {
                String name = occupation.getPlayerName();
                if (name != null && !name.isEmpty()) tag.putString(TAG_OCCUPIER, name);
            }
            if (this.customName != null) tag.putString(TAG_NAME, this.customName);
        }
        return tag;
    }

    // UP: 上半球 -90 ~ 0
    // DOWN: 下半球 0 ~ 90
    @Override
    public float getMaxPitch() {
        return this.getBlockState().getValue(BioCameraBlock.FACING) == Direction.UP ? 0 : 90;
    }

    @Override
    public float getMinPitch() {
        return this.getBlockState().getValue(BioCameraBlock.FACING) == Direction.UP ? -90 : 0;
    }

    @Override
    public float getDefaultPitch() {
        return this.getBlockState().getValue(BioCameraBlock.FACING) == Direction.UP ? -30 : 30;
    }

    @Override
    public Vec3 getCameraPosition(float yaw, float pitch) {
        return this.getBlockPos().getCenter();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        if (this.customName != null) {
            tag.putString(TAG_NAME, customName);
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

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (this.level instanceof ServerLevel serverLevel)
            BioCameraManager.get(serverLevel).releaseCamera(this.getBlockPos());
    }

    public void setName(String name){
        String normalized = name == null ? "" : name.trim();
        this.customName = normalized.isEmpty() ? null : normalized;
        if (this.level instanceof ServerLevel serverLevel){
            this.setChanged();
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
            BioMachineryService.broadcastBioMachineUpdate(serverLevel, BioMachineData.of(this));
        }
    }

    @Override
    public @Nullable String getName() {
        return this.customName == null ? this.getDefaultName() : this.customName;
    }
}
