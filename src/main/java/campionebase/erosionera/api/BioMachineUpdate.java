package campionebase.erosionera.api;

import campionebase.erosionera.registry.BioMachineTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record BioMachineUpdate(
        @NotNull Operation operation,
        @NotNull BioMachineType<?> type,
        @NotNull BlockPos pos,
        @NotNull BioMachinePatch patch
) {
    public enum Operation {
        ADD,
        CHANGE,
        REMOVE
    }

    public static BioMachineUpdate added(@NotNull BioMachineData data) {
        return new BioMachineUpdate(Operation.ADD, data.type(), data.pos(),
                BioMachinePatch.full(data.customData()));
    }

    public static @Nullable BioMachineUpdate changed(@NotNull BioMachineData oldData,
                                                     @NotNull BioMachineData newData) {
        if (!oldData.type().equals(newData.type()) || !oldData.pos().equals(newData.pos())) {
            return added(newData);
        }
        BioMachinePatch patch = BioMachinePatch.diff(oldData.customData(), newData.customData());
        return patch.isEmpty()
                ? null
                : new BioMachineUpdate(Operation.CHANGE, newData.type(), newData.pos(), patch);
    }

    public static BioMachineUpdate removed(@NotNull BioMachineData data) {
        return new BioMachineUpdate(Operation.REMOVE, data.type(), data.pos(), BioMachinePatch.EMPTY);
    }

    public @Nullable BioMachineData apply(@Nullable BioMachineData current) {
        if (this.operation == Operation.REMOVE) return null;

        CompoundTag customData = this.patch.apply(
                current == null ? new CompoundTag() : current.customData());
        return new BioMachineData(this.type, this.pos, customData);
    }

    public static void save(@NotNull FriendlyByteBuf buf, BioMachineUpdate update) {
        buf.writeEnum(update.operation());
        buf.writeRegistryId(BioMachineTypes.REGISTRY.get(), update.type());
        buf.writeBlockPos(update.pos());
        BioMachinePatch.save(buf, update.patch());
    }

    public static BioMachineUpdate load(@NotNull FriendlyByteBuf buf) {
        Operation operation = buf.readEnum(Operation.class);
        BioMachineType<?> type = buf.readRegistryId();
        BlockPos pos = buf.readBlockPos();
        return new BioMachineUpdate(operation, type, pos, BioMachinePatch.load(buf));
    }
}
