package campionebase.erosionera.block.entity;

import campionebase.erosionera.api.IBioMachine;
import campionebase.erosionera.block.BioConnectorBlock;
import campionebase.erosionera.registry.ErErBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BioConnectorBlockEntity extends AbstractBioConnectorBlockEntity{
    public BioConnectorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ErErBlockEntities.BIO_CONNECTOR.get(), pos, blockState);
    }

    public IBioMachine getMachinery() {
        BlockPos pos = this.getBlockPos().relative(this.getBlockState().getValue(BioConnectorBlock.FACING).getOpposite());
        if (this.level == null) return null;
        return this.level.getBlockEntity(pos) instanceof IBioMachine machinery ? machinery : null;
    }
}
