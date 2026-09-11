package campionebase.erosionera.inventory;

import campionebase.erosionera.api.*;
import campionebase.erosionera.network.BioMachineryNetwork;
import campionebase.erosionera.network.packet.BioCameraActionPacket;
import campionebase.erosionera.network.packet.BioCameraOccupationPacket;
import campionebase.erosionera.network.packet.BioCameraPickPacket;
import campionebase.erosionera.network.packet.BioControllerReleasePacket;
import campionebase.erosionera.registry.ErErBlocks;
import campionebase.erosionera.registry.ErErMenuTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class BioControllerMenu extends AbstractContainerMenu implements IBioMachineController<IBioCamera> {
    // 机械列表原始数据 玩家当前维度
    private final Set<BioMachineData> dataSet = new HashSet<>();
    private int dataVersion = 0;

    private final Queue<BioMachineData> updateQueue = new ConcurrentLinkedQueue<>();
    @NotNull
    private final BlockPos controllerPos;
    @NotNull
    private final Level level;

    // 当前摄像机（核心事实）
    private @Nullable IBioCamera currentCamera = null;
    public float cameraYaw = 0.0F;
    public float cameraPitch = 0.0F;

    public BioControllerMenu(int windowId, Inventory inventory, FriendlyByteBuf buf){
        this(windowId, inventory.player.level(), buf.readBlockPos());
    }

    public BioControllerMenu(int windowId, @NotNull Level level, @NotNull BlockPos pos){
        super(ErErMenuTypes.BIO_CONTROLLER_MENU.get(), windowId);
        this.level = level;
        this.controllerPos = pos;
    }

    @Nullable
    public IBioController getController(){
        if (this.level.getBlockEntity(this.controllerPos) instanceof IBioController controller)
            return controller;
        return null;
    }

    @Nullable
    public IBioCore getCore(){
        if (this.getController() == null) return null;
        return this.getController().getCore();
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

    public void refreshData(Set<BioMachineData> machineSet){
        this.dataSet.clear();
        this.dataSet.addAll(machineSet);
        this.dataVersion ++;
    }

    public Set<BioMachineData> getDataSet(){
        return this.dataSet;
    }

    public void enqueueMessage(BioMachineData data){
        this.updateQueue.add(data);
    }

    public void drainUpdateQueue(Consumer<BioMachineData> consumer){
        BioMachineData update;
        while ((update = this.updateQueue.poll()) != null){
            consumer.accept(update);
        }
    }

    private void resetViewDirection(){
        if (this.currentCamera == null) return;
        if (this.level.isClientSide){
            this.cameraYaw = this.currentCamera.getDefaultYaw();
            this.cameraPitch = this.currentCamera.getDefaultPitch();
        }
    }

    public void respondCameraOccupation(BioCameraOccupationPacket.ResultState state, @Nullable BlockPos cameraPos) {

    }

    public void action(IBioController.Action action){
        if (this.currentCamera != null){
            if (this.level instanceof ClientLevel){
                BioMachineryNetwork.INSTANCE.sendToServer(new BioCameraActionPacket(
                        this.currentCamera.getBlockPos(),
                        action, this.cameraYaw, this.cameraPitch
                ));
            }
        }
    }

    public void requestPick(boolean clear){
        if (this.currentCamera == null) return;
        if (this.level instanceof ClientLevel){
            BioMachineryNetwork.INSTANCE.sendToServer(new BioCameraPickPacket(
                    this.currentCamera.getBlockPos(),
                    this.cameraYaw,
                    this.cameraPitch,
                    clear
            ));
        }
    }

    public BlockPos getBlockPos(){
        return this.controllerPos;
    }

    @Nullable
    public IBioCamera getCamera(){
        return this.currentCamera;
    }

    public void setCamera(IBioCamera camera) {
        this.currentCamera = camera;
    }

    public void exit() {
        if (this.level.isClientSide) {
            BioMachineryNetwork.INSTANCE.sendToServer(new BioControllerReleasePacket(this.controllerPos));
        } else if (this.level.getBlockEntity(this.controllerPos) instanceof IBioController controller){
            controller.onReleased();
        }
    }
}
