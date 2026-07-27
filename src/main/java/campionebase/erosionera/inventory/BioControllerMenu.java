package campionebase.erosionera.inventory;

import campionebase.erosionera.registry.ErErBlocks;
import campionebase.erosionera.registry.ErErMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BioControllerMenu extends AbstractContainerMenu {
    private final BlockPos controllerPos;
    private final Level level;
    private final List<BlockPos> cameras = new ArrayList<>();
    // -1 表示“主视角”，0+ 表示摄像机列表索引
    private int selectedIndex = -1;

    public BioControllerMenu(int windowId, Inventory inventory, FriendlyByteBuf buf){
        this(windowId, buf.readBlockPos(), inventory.player.level());
    }

    public BioControllerMenu(int windowId, BlockPos pos, Level level){
        super(ErErMenuTypes.BIO_CONTROLLER_MENU.get(), windowId);
        this.controllerPos = pos;
        this.level = level;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return this.level.getBlockState(this.controllerPos).is(ErErBlocks.BIO_CONTROLLER.get()) &&
                player.distanceToSqr(this.controllerPos.getCenter()) <= 64.0;
    }



    public void updateCameraList(Set<BlockPos> cameras){
        this.cameras.clear();
        this.cameras.addAll(cameras);
        if (this.selectedIndex >= cameras.size()) {
            this.selectedIndex = -1;
        }
    }

    public void selectNext() {
        int max = this.cameras.size() - 1;
        if (this.selectedIndex >= max) {
            this.selectedIndex = -1;
        } else {
            this.selectedIndex++;
        }
    }

    public void selectPrev() {
        int max = this.cameras.size() - 1;
        if (this.selectedIndex <= -1) {
            this.selectedIndex = max;
        } else {
            this.selectedIndex--;
        }
    }

    public List<BlockPos> getCameras() {
        return this.cameras;
    }

    public BlockPos getBlockPos(){
        return this.controllerPos;
    }

    public int getSelectedIndex() {
        return this.selectedIndex;
    }
}
