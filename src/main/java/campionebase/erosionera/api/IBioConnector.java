package campionebase.erosionera.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IBioConnector {
    @NotNull BlockPos getBlockPos();

    @Nullable IBioMachine getMachine();
}
