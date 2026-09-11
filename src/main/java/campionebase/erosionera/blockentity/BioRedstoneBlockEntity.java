package campionebase.erosionera.blockentity;

import campionebase.erosionera.api.BioMachineType;
import campionebase.erosionera.api.IBioControllable;
import campionebase.erosionera.api.IBioController;
import campionebase.erosionera.api.IBioMachine;
import campionebase.erosionera.block.BioRedstoneBlock;
import campionebase.erosionera.registry.ErErBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BioRedstoneBlockEntity extends BlockEntity implements IBioMachine, IBioControllable {
    public BioRedstoneBlockEntity(BlockPos pos, BlockState blockState) {
        super(ErErBlockEntities.BIO_REDSTONE.get(), pos, blockState);
    }

    @Override
    public void onControlledAction(ServerPlayer player, IBioController.Action action) {
        if (this.level == null || this.level.isClientSide) return;

        BlockState blockState = this.getBlockState();
        int power = blockState.getValue(BioRedstoneBlock.POWER);

        if (action == IBioController.Action.INCREMENT && power < 15){
            power += 1;
        } else if (action == IBioController.Action.DECREMENT && power > 0){
            power -= 1;
        } else {
            return;
        }

        this.level.setBlock(worldPosition, blockState.setValue(BioRedstoneBlock.POWER, power), 3);
        this.level.updateNeighborsAt(worldPosition, blockState.getBlock());
    }

    @Override
    public @NotNull BioMachineType<? extends IBioMachine> getMachineType() {
        return BioMachineType.EMPTY;
    }
}
