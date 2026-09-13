package campionebase.erosionera.api;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public interface IBioMachine {
    String TAG_LOADED = "is_loaded";

    BlockPos getBlockPos();
    @NotNull
    BioMachineType<? extends IBioMachine> getMachineType();

    default boolean equals(IBioMachine machine){
        if (machine == null) return false;
        return this.getBlockPos().equals(machine.getBlockPos()) &&
                this.getMachineType().equals(machine.getMachineType());
    }

    default CompoundTag getCustomData(){
        CompoundTag tag = new CompoundTag();
        if (this instanceof BlockEntity be && be.hasLevel()) {
            tag.putBoolean(TAG_LOADED, true);
        }
        return tag;
    }
}
