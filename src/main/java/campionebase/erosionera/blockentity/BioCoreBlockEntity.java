package campionebase.erosionera.blockentity;

import campionebase.erosionera.api.IBioController;
import campionebase.erosionera.api.IBioCore;
import campionebase.erosionera.api.IBioMachine;
import campionebase.erosionera.network.BioMachineryService;
import campionebase.erosionera.registry.ErErBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class BioCoreBlockEntity extends BlockEntity implements IBioCore {

    public BioCoreBlockEntity(BlockPos pos, BlockState blockState) {
        super(ErErBlockEntities.BIO_CORE.get(), pos, blockState);
    }

    @Override
    @Nullable
    public IBioController getController() {
        BlockPos controllerPos = this.getBlockPos().above();
        if (this.level == null) return null;
        if (this.level.getBlockEntity(controllerPos) instanceof IBioController controller) return controller;
        return null;
    }

    public @NotNull Set<IBioMachine> getConnectedMachines(){
        if (!(this.level instanceof ServerLevel level)) return Set.of();
        return BioMachineryService.findAllConnectedByConnector(level, this.getBlockPos());
    }
}
