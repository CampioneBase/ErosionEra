package campionebase.erosionera.block.entity;

import campionebase.erosionera.api.IBioCamera;
import campionebase.erosionera.api.IBioMachine;
import campionebase.erosionera.registry.ErErBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class BioCameraBlockEntity extends AbstractBioConnectorBlockEntity implements IBioMachine, IBioCamera {
    public BioCameraBlockEntity(BlockPos pos, BlockState blockState) {
        super(ErErBlockEntities.BIO_CAMERA.get(), pos, blockState);
    }

    @Override
    public IBioMachine getMachinery() {
        return this;
    }

    @Override
    public boolean isCore() {
        return false;
    }

    @Override
    public Vec3 getCameraPos() {
        return this.getBlockPos().getCenter();
    }
}
