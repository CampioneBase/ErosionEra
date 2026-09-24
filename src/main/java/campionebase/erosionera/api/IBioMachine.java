package campionebase.erosionera.api;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public interface IBioMachine {
    String TAG_LOADED = "is_loaded";
    String TAG_ID = "id";

    BlockPos getBlockPos();
    @NotNull
    BioMachineType<? extends IBioMachine> getMachineType();
    @NotNull
    default ResourceLocation getId(){
        return this.getMachineType().getId();
    }

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
        tag.putString(TAG_ID, this.getId().toString());
        return tag;
    }
}
