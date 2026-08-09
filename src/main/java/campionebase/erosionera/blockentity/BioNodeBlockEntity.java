package campionebase.erosionera.blockentity;

import campionebase.erosionera.api.IBioConnector;
import campionebase.erosionera.api.IBioMachine;
import campionebase.erosionera.registry.ErErBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BioNodeBlockEntity extends BlockEntity implements IBioConnector {
    public BioNodeBlockEntity(BlockPos pos, BlockState blockState) {
        super(ErErBlockEntities.BIO_NODE.get(), pos, blockState);
    }

    @Override
    public @Nullable IBioMachine getMachine() {
        return null;
    }
}
