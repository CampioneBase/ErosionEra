package campionebase.erosionera.inventory;

import campionebase.erosionera.block.BioCameraBlock;
import campionebase.erosionera.registry.ErErBlocks;
import campionebase.erosionera.registry.ErErMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BioControllerMenu extends AbstractContainerMenu {
    private final BlockPos controllerPos;
    private final Level level;
    private final List<BlockPos> cameras = new ArrayList<>();
    // -1 表示“主视角”，0+ 表示摄像机列表索引
    private int selectedIndex = -1;

    public float cameraYaw = 0.0F;
    public float cameraPitch = 0.0F;
    @Nullable
    public BlockPos cameraPos = null;

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
            this.select(-1);
        } else {
            this.select(this.selectedIndex + 1);
        }
    }

    public void selectPrev() {
        int max = this.cameras.size() - 1;
        if (this.selectedIndex <= -1) {
            this.select(max);
        } else {
            this.select(this.selectedIndex - 1);
        }
    }

    private void select(int index){
        index = Mth.clamp(index, -1, this.cameras.size() - 1);
        this.selectedIndex = index;
        if (index == -1) {
            this.cameraPos = null;
        } else {
            this.cameraPos = this.cameras.get(index);
            this.resetViewDirection();
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

    private void resetViewDirection(){
        if (this.cameraPos == null) return;
        if (this.level.isClientSide){
            BlockState state = this.level.getBlockState(this.cameraPos);
            if (state.hasProperty(BioCameraBlock.FACING)) {
                Direction facing = state.getValue(BioCameraBlock.FACING);
                // 默认水平看向 Z 正方向（yaw=0）
                this.cameraYaw = 0.0F;
                if (facing == Direction.UP) {
                    this.cameraPitch = -30.0F; // 正上方
                } else if (facing == Direction.DOWN) {
                    this.cameraPitch = 30.0F;  // 正下方
                }
            }
        }
    }

    @Nullable
    public BlockPos getCameraPos(){
        return this.cameraPos;
    }

    @Nullable
    public Direction getCameraFacing(){
        if (this.cameraPos == null || !this.level.isClientSide) return null;
        BlockState blockState = this.level.getBlockState(this.cameraPos);
        if (blockState.hasProperty(BioCameraBlock.FACING))
            return blockState.getValue(BioCameraBlock.FACING);
        return null;
    }

    public void exit() {
        this.select(-1);
    }
}
