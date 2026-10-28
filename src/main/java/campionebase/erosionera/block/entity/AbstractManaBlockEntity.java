package campionebase.erosionera.block.entity;

import campionebase.erosionera.magic.resource.IManaStorage;
import campionebase.erosionera.magic.resource.ManaStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractManaBlockEntity extends BlockEntity {

    protected final IManaStorage manaStorage;

    public AbstractManaBlockEntity(BlockEntityType<?> type,
                                   BlockPos pos,
                                   BlockState blockState,
                                   int manaCapacity

    ) {
        super(type, pos, blockState);
        this.manaStorage = ManaStorage.of(manaCapacity, /* todo just test */ manaCapacity);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        if(tag.get(ManaStorage.TAG_MANA_STORAGE) instanceof IntTag intTag){
            this.manaStorage.deserializeNBT(intTag);
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(ManaStorage.TAG_MANA_STORAGE, this.manaStorage.serializeNBT());
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.put(ManaStorage.TAG_MANA_STORAGE, this.manaStorage.serializeNBT());
        return tag;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == IManaStorage.CAPABILITY)
            return LazyOptional.of(() -> this.manaStorage).cast();

        return super.getCapability(cap, side);
    }
}
