package campionebase.erosionera.api;

import campionebase.erosionera.registry.BioMachineTypes;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface IBioCore extends IBioMachine {
    @Override
    @NotNull
    default BioMachineType<? extends IBioCore> getMachineType() {
        return BioMachineTypes.CORE.get();
    }

    @Nullable
    IBioController getController();
    /**
     * 获取所有与核心相连的机械
     * @return 相连的Bio Machine集合
     */
    @NotNull
    Set<IBioMachine> getConnectedMachines();
}
