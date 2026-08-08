package campionebase.erosionera.blockentity;

import campionebase.erosionera.api.IBioControllable;
import campionebase.erosionera.block.BioRedstoneBlock;
import campionebase.erosionera.registry.ErErBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BioRedstoneBlockEntity extends BlockEntity implements IBioControllable {
    public BioRedstoneBlockEntity(BlockPos pos, BlockState blockState) {
        super(ErErBlockEntities.BIO_REDSTONE.get(), pos, blockState);
    }

    @Override
    public void onControlledAction(ServerPlayer player, ControlAction action) {
        if (this.level == null || this.level.isClientSide) return;

        BlockState blockState = this.getBlockState();
        int power = blockState.getValue(BioRedstoneBlock.POWER);

        if (action == ControlAction.INCREMENT && power < 15){
            power += 1;
        } else if (action == ControlAction.DECREMENT && power > 0){
            power -= 1;
        } else {
            return;
        }

        // 更新方块状态（红石信号会自动传播）
        this.level.setBlock(worldPosition, blockState.setValue(BioRedstoneBlock.POWER, power), 3);
        // 通知邻居方块更新（例如红石灯、红石粉）
        this.level.updateNeighborsAt(worldPosition, blockState.getBlock());
    }
}
