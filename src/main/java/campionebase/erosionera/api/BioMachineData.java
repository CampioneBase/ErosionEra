package campionebase.erosionera.api;

import campionebase.erosionera.registry.BioMachineTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

public record BioMachineData(
        @NotNull BioMachineType<?> type,
        @NotNull BlockPos pos,
        @NotNull CompoundTag customData
) {
    public static BioMachineData of(IBioMachine machine){
        return new BioMachineData(
                machine.getMachineType(),
                machine.getBlockPos(),
                machine.getCustomData()
        );
    }

    public static BioMachineData of(IBioMachine machine, CompoundTag tag){
        return new BioMachineData(
                machine.getMachineType(),
                machine.getBlockPos(),
                tag
        );
    }

    public static void save(FriendlyByteBuf buf, BioMachineData data){
        buf.writeRegistryId(BioMachineTypes.REGISTRY.get(), data.type);
        buf.writeBlockPos(data.pos);
        buf.writeNbt(data.customData);
    }

    public static BioMachineData load(FriendlyByteBuf buf){
        BioMachineType<?> type = buf.readRegistryId();
        BlockPos pos = buf.readBlockPos();
        CompoundTag tag = buf.readNbt();

        return new BioMachineData(
                type,
                pos,
                tag == null ? new CompoundTag() : tag
        );
    }
}
