package campionebase.erosionera.api;

import campionebase.erosionera.registry.BioMachineTypes;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

public interface IBioStorage extends IBioMachine {

    @Override
    @NotNull
    default BioMachineType<IBioStorage> getMachineType(){
        return BioMachineTypes.STORAGE.get();
    }
}
