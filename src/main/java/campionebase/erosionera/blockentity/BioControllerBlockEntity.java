package campionebase.erosionera.blockentity;

import campionebase.erosionera.api.IBioControllable;
import campionebase.erosionera.api.IBioController;
import campionebase.erosionera.block.BioControllerBlock;
import campionebase.erosionera.network.BioMachineryService;
import campionebase.erosionera.registry.ErErBlockEntities;
import campionebase.erosionera.inventory.BioControllerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BioControllerBlockEntity extends BioMachineBlockEntity implements MenuProvider, IBioController {

    @Nullable
    private Player owner; // 服务端数据

    @Nullable
    private Entity target;

    public BioControllerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ErErBlockEntities.BIO_CONTROLLER.get(), pos, blockState);
    }

    @Override
    public @Nullable Player getUser() {
        return this.owner;
    }

    @Override
    public void onReleased() {
        this.owner = null;
        if (this.level != null && !this.level.isClientSide){
            this.level.sendBlockUpdated(this.getBlockPos(),
                    this.getBlockState(),
                    this.getBlockState().setValue(BioControllerBlock.OCCUPIED, false),
                    Block.UPDATE_ALL);
            this.setChanged();
        }
    }

    @Override
    public void control(IBioControllable target, Action action) {
        if (this.level instanceof ServerLevel && this.owner instanceof ServerPlayer player) {
            target.onControlledAction(player, action);
        }
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable(this.getBlockState().getBlock().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory inventory, @NotNull Player player) {
        this.owner = player;
        return new BioControllerMenu(windowId, this.getBlockPos(), this.level);
    }
}
