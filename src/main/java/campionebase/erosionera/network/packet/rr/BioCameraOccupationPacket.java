package campionebase.erosionera.network.packet.rr;

import campionebase.erosionera.api.BioMachineData;
import campionebase.erosionera.api.IBioCamera;
import campionebase.erosionera.api.IBioCore;
import campionebase.erosionera.client.screen.BioControllerScreen;
import campionebase.erosionera.network.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

// 控制器对摄像机
public class BioCameraOccupationPacket {
    public enum ResultState{
        /** 成功占用 */
        SUCCESS,
        /** 不可用 */
        INVALID,
        /** 已被占用 */
        OCCUPIED
    }
    /**
     * 客户端向服务端发送占用请求
     * @param oldPos 释放的摄像机坐标（原先摄像机选择）
     * @param newPos 占用的摄像机坐标（请求摄像机选择）
     * @param core 发起请求的控制器
     */
    public static record Request(@Nullable BlockPos oldPos, @Nullable BlockPos newPos, BlockPos core){
        public static void encode(Request packet, FriendlyByteBuf buf) {
            buf.writeNullable(packet.oldPos, FriendlyByteBuf::writeBlockPos);
            buf.writeNullable(packet.newPos, FriendlyByteBuf::writeBlockPos);
            buf.writeBlockPos(packet.core);
        }

        public static Request decode(FriendlyByteBuf buf) {
            return new Request(
                    buf.readNullable(FriendlyByteBuf::readBlockPos),
                    buf.readNullable(FriendlyByteBuf::readBlockPos),
                    buf.readBlockPos()
            );
        }

        public static void handle(Request packet, Supplier<NetworkEvent.Context> contextSupplier) {
            NetworkEvent.Context context = contextSupplier.get();
            ServerPlayer sender = context.getSender();
            if (sender == null) return;
            BlockPos newPos = packet.newPos;
            BlockPos oldPos = packet.oldPos;
            if (Objects.equals(oldPos, newPos)) {
                // 没变化。基本属于正常流程会出现的情况，不会发出警告也不会向主线程添加任务
                context.setPacketHandled(true);
                return;
            }
            context.enqueueWork(() -> {
                ServerLevel level = sender.serverLevel();
                BlockPos corePos = packet.core;
                if (newPos == null) {
                    BioMachineryService.releaseCameraIfOwned(level, oldPos, sender);
                    sendResponse(sender, ResultState.SUCCESS, null);
                    return;
                }
                // 新摄像机是否有效
                if (!(level.getBlockEntity(newPos) instanceof IBioCamera newCamera)){
                    BioMachineryNetwork.LOGGER.warn(
                            "Preventing {} from occupying camera: new camera invalid",
                            sender.getName().getString()
                    );
                    sendResponse(sender, ResultState.INVALID, newPos);
                    return;
                }
                // 指令源的控制器是否有效
                if (!(level.getBlockEntity(corePos) instanceof IBioCore core) || core.getController() == null) {
                    BioMachineryNetwork.LOGGER.warn(
                            "Preventing {} from occupying camera: controller invalid",
                            sender.getName().getString()
                    );
                    sendResponse(sender, ResultState.INVALID, newPos);
                    return;
                }
                // 检验是否为本人
                Player user = core.getController().getUser();
                if (user == null || !sender.getUUID().equals(user.getUUID())){
                    BioMachineryNetwork.LOGGER.warn(
                            "Preventing {} from operating a controller held by another user",
                            sender.getName().getString()
                    );
                    sendResponse(sender, ResultState.INVALID, newPos);
                    return;
                }

                // 摄像机是否与核心相连
                if (!BioMachineryService.isConnected(level, newPos, corePos)){
                    BioMachineryNetwork.LOGGER.warn(
                            "Preventing {} from occupying camera: camera disconnected",
                            sender.getName().getString()
                    );
                    sendResponse(sender, ResultState.INVALID, newPos);
                    return;
                }
                // 占用请求
                Player occupier = BioCameraManager.get(level).tryOccupyCamera(newPos, core.getController());
                if (occupier == null) {
                    BioMachineryNetwork.LOGGER.warn(
                            "Preventing {} from occupying a camera: user not found.",
                            sender.getName().getString()
                    );
                    sendResponse(sender, ResultState.INVALID, newPos);
                    return;
                }
                // 确认发送玩家与事实相符
                if (sender.getUUID().equals(occupier.getUUID())){
                    // 成功占用
                    BioMachineryService.releaseCameraIfOwned(level, oldPos, sender);
                    sendResponse(sender, ResultState.SUCCESS, newPos);
                    // 广播占用
                    BioMachineryService.broadcastBioMachineUpdate(level, BioMachineData.of(newCamera));
                } else {
                    // 以占用事实为主
                    sendResponse(sender, ResultState.OCCUPIED, newPos);
                    BioMachineryService.broadcastBioMachineUpdate(level, BioMachineData.of(newCamera));
                }
            });
            context.setPacketHandled(true);
        }
    }

    private static void sendResponse(ServerPlayer player, ResultState state, @Nullable BlockPos pos){
        BioMachineryNetwork.INSTANCE.send(
                PacketDistributor.PLAYER.with(() -> player),
                new Response(state, pos)
        );
    }

    /**
     * 服务端回应客户端占用请求
     * @param resultState 回应状态：{@code SUCCESS | OCCUPIED | INVALID}
     * @param camera 目标摄像机，{@code null} 表示为主视角（不占用任何摄像机）
     */
    public static record Response(ResultState resultState, @Nullable BlockPos camera){
        public static void encode(Response packet, FriendlyByteBuf buf) {
            buf.writeEnum(packet.resultState);
            buf.writeNullable(packet.camera, FriendlyByteBuf::writeBlockPos);
        }

        public static Response decode(FriendlyByteBuf buf) {
            return new Response(
                    buf.readEnum(ResultState.class),
                    buf.readNullable(FriendlyByteBuf::readBlockPos)
            );
        }

        public static void handle(Response packet, Supplier<NetworkEvent.Context> contextSupplier) {
            NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> {
                LocalPlayer player = Minecraft.getInstance().player;
                if (player == null) return;
                if (Minecraft.getInstance().screen instanceof BioControllerScreen screen){
                    screen.onCameraOccupationResponse(packet.resultState, packet.camera);
                }

            });
            context.setPacketHandled(true);
        }
    }
}
