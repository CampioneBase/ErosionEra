package campionebase.erosionera.api;

import net.minecraft.core.BlockPos;

public interface IBioMachine {
    boolean isCore();
    BlockPos getBlockPos();

    default boolean equals(IBioMachine machine){
        if (machine == null) return false;
        return this.getBlockPos().equals(machine.getBlockPos());
    }
}
