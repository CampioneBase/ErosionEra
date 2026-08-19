package campionebase.erosionera.blockentity;

import campionebase.erosionera.api.IBioMachine;
import campionebase.erosionera.block.BioConnectorBlock;
import campionebase.erosionera.registry.ErErBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class BioConnectorBlockEntity extends AbstractBioConnectorBlockEntity{
    public BioConnectorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ErErBlockEntities.BIO_CONNECTOR.get(), pos, blockState);
    }

    public IBioMachine getMachine() {
        BlockPos pos = this.getBlockPos().relative(this.getBlockState().getValue(BioConnectorBlock.FACING).getOpposite());
        if (this.level == null) return null;
        return this.level.getBlockEntity(pos) instanceof IBioMachine machinery ? machinery : null;
    }

    @Override
    public @NotNull Vec3 getWirePos(BlockPos neighborPos) {
        if (!this.getBlockState().hasProperty(BioConnectorBlock.FACING)) return super.getWirePos(neighborPos);
        Vec3i normal = this.getBlockState().getValue(BioConnectorBlock.FACING).getNormal();
        Vec3 offset = new Vec3(normal.getX(), normal.getY(), normal.getZ()).scale(0.2);
        return super.getWirePos(neighborPos).add(offset);
    }
}
