package campionebase.erosionera.inventory;

import campionebase.erosionera.api.IBioCamera;
import campionebase.erosionera.api.IBioControllable;
import campionebase.erosionera.api.IBioController;
import campionebase.erosionera.network.BioMachineryNetwork;
import campionebase.erosionera.network.packet.BioCameraActionPacket;
import campionebase.erosionera.network.packet.BioCameraOccupationPacket;
import campionebase.erosionera.network.packet.BioControllerReleasePacket;
import campionebase.erosionera.registry.ErErBlocks;
import campionebase.erosionera.registry.ErErMenuTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BioControllerMenu extends AbstractContainerMenu {
    private final BlockPos controllerPos;
    private final Level level;
    private final List<CameraInfo> cameras = new ArrayList<>();

    public record CameraInfo(
            @NotNull IBioCamera camera,
            @Nullable String username
    ){}
    // -1 表示“主视角”，0+ 表示摄像机列表索引
    private int selectedIndex = -1;
    // 当前摄像机（核心事实）
    private @Nullable IBioCamera currentCamera = null;

    public float cameraYaw = 0.0F;
    public float cameraPitch = 0.0F;

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

    public void refreshCameraOccupation(Map<@NotNull IBioCamera, @Nullable String> cameraOccupation){
        this.cameras.clear();
        cameraOccupation.forEach((k, v) -> this.cameras.add(new CameraInfo(k, v)));
        this.cameras.sort(Comparator.comparing(a -> a.camera.getBlockPos()));
        this.select(this.currentCamera); // 刷新索引
    }

    private void resetViewDirection(){
        if (this.currentCamera == null) return;
        if (this.level.isClientSide){
            this.cameraYaw = this.currentCamera.getDefaultYaw();
            this.cameraPitch = this.currentCamera.getDefaultPitch();
        }
    }

    private int indexOf(@Nullable IBioCamera camera){
        if (camera == null) return -1;
        for (int i = 0; i < this.cameras.size(); i ++){
            if (this.cameras.get(i).camera.equals(camera)) return i;
        }
        return -1;
    }

    private void select(@Nullable IBioCamera camera) {
        boolean flag = Objects.equals(this.currentCamera, camera);
        this.selectedIndex = this.indexOf(camera);
        // 选定的摄像机不存在于服务端返回的可用摄像机列表，表示服务端已经处理过摄像机占用数据
        this.currentCamera = this.selectedIndex == -1 ? null : camera;
        if (!flag) this.resetViewDirection();
    }

    public void selectNext() {
        int index = this.selectedIndex;
        while (true) {
            index ++;
            if (index > this.cameras.size() - 1) index = -1;
            if (index == -1 || this.cameras.get(index).username == null) {
                this.requestSelecting(index);
                return;
            }
        }
    }

    public void selectPrev() {
        int index = this.selectedIndex;
        while (true) {
            index --;
            if (index < -1) index = this.cameras.size() -1;
            if (index == -1 || this.cameras.get(index).username == null){
                this.requestSelecting(index);
                return;
            }
        }
    }

    private void requestSelecting(int index){
        if (this.selectedIndex == index) return;
        index = Mth.clamp(index, -1, this.cameras.size() - 1);
        BlockPos requestPos = index == -1 ? null : this.cameras.get(index).camera.getBlockPos();
        // 向服务端发送占用请求
        if (this.level.isClientSide){
            BioMachineryNetwork.INSTANCE.sendToServer(new BioCameraOccupationPacket.Request(
                    this.currentCamera == null ? null : this.currentCamera.getBlockPos(),
                    requestPos,
                    this.controllerPos
            ));
        }
    }

    public void respondSelecting(BioCameraOccupationPacket.ResultState state, @Nullable BlockPos cameraPos) {
        switch (state){
            case SUCCESS -> {
                if (cameraPos == null) {
                    this.select(null);
                    return;
                }
                // 一般来说，返回的摄像机坐标就是客户端选择的坐标且在服务端通过验证的
                // 这要是不一致，就是客户端和服务端一起犯病
                if (this.level.getBlockEntity(cameraPos) instanceof IBioCamera camera){
                    this.select(camera);
                } else {
                    this.select(null);
                }
            }
            case OCCUPIED -> {
                if (cameraPos == null) return;
                // 添加额外信息
                if (this.level.getBlockEntity(cameraPos) instanceof IBioCamera camera){
                    int index = this.indexOf(camera);
                    if (index >= 0) {
                        this.cameras.set(index, new CameraInfo(camera, "*Occupied*"));
                    }
                }
            }
            case INVALID -> {
                if (cameraPos == null) return;
                // 添加额外信息
                if (this.level.getBlockEntity(cameraPos) instanceof IBioCamera camera){
                    int index = this.indexOf(camera);
                    if (index >= -1) {
                        this.cameras.set(index, new CameraInfo(camera, "*Invalid*"));
                    }
                }
            }
        }
    }

    public void action(IBioControllable.ControlAction action){
        if (this.currentCamera != null){
            if (this.level instanceof ClientLevel){
                BioMachineryNetwork.INSTANCE.sendToServer(new BioCameraActionPacket(
                        this.currentCamera.getBlockPos(),
                        action, this.cameraYaw, this.cameraPitch
                ));
            }
        }
    }

    public List<CameraInfo> getCameras() {
        return this.cameras;
    }

    public BlockPos getBlockPos(){
        return this.controllerPos;
    }

    public int getSelectedIndex() {
        return this.selectedIndex;
    }

    @Nullable
    public IBioCamera getCamera(){
        return this.currentCamera;
    }

    public void exit() {
        this.requestSelecting(-1);
        if (this.level.getBlockEntity(this.controllerPos) instanceof IBioController controller){
            if (this.level.isClientSide) {
                BioMachineryNetwork.INSTANCE.sendToServer(new BioControllerReleasePacket(this.controllerPos));
            }
            controller.onReleased();
        }
    }
}
