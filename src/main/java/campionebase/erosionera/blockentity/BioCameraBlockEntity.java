package campionebase.erosionera.blockentity;

import campionebase.erosionera.api.IBioCamera;
import campionebase.erosionera.api.IBioMachine;
import campionebase.erosionera.block.BioCameraBlock;
import campionebase.erosionera.registry.ErErBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class BioCameraBlockEntity extends AbstractBioConnectorBlockEntity implements IBioCamera {
    public BioCameraBlockEntity(BlockPos pos, BlockState blockState) {
        super(ErErBlockEntities.BIO_CAMERA.get(), pos, blockState);
    }

    @Override
    public IBioMachine getMachinery() {
        return this;
    }

    // UP: 上半球 -90 ~ 0
    // DOWN: 下半球 0 ~ 90
    @Override
    public float getMaxPitch() {
        return this.getBlockState().getValue(BioCameraBlock.FACING) == Direction.UP ? 0 : 90;
    }

    @Override
    public float getMinPitch() {
        return this.getBlockState().getValue(BioCameraBlock.FACING) == Direction.UP ? -90 : 0;
    }

    public float getDefaultPitch() {
        return this.getBlockState().getValue(BioCameraBlock.FACING) == Direction.UP ? -30 : 30;
    }

    @Override
    public Vec3 getCameraPosition(float yaw, float pitch) {
        return this.getBlockPos().getCenter();
    }

    @Override
    public Component getName() {
        return Component.literal("Camera[" + this.getBlockPos().toShortString() + "]");
    }
}
