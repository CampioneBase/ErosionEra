package campionebase.erosionera.network.packet;

import campionebase.erosionera.api.IBioCamera;
import campionebase.erosionera.api.IBioController;
import campionebase.erosionera.api.IBioMachine;
import campionebase.erosionera.inventory.BioControllerMenu;
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
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

// 控制器对摄像机
public class OccupyBioCameraPacket {
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
     * @param oldCamera 释放的摄像机坐标（原先摄像机选择）
     * @param newCamera 占用的摄像机坐标（请求摄像机选择）
     * @param controller 发起请求的控制器
     */
    public static record Request(@Nullable BlockPos oldCamera, @Nullable BlockPos newCamera, BlockPos controller){
        public static void encode(Request packet, FriendlyByteBuf buf) {
            buf.writeNullable(packet.oldCamera, FriendlyByteBuf::writeBlockPos);
            buf.writeNullable(packet.newCamera, FriendlyByteBuf::writeBlockPos);
            buf.writeBlockPos(packet.controller);
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
            context.enqueueWork(() -> {
                BlockPos newPos = packet.newCamera;
                BlockPos oldPos = packet.oldCamera;
                if (Objects.equals(oldPos, newPos)) return; // 没变化
                ServerPlayer sender = context.getSender();
                if (sender == null) return;
                ServerLevel level = sender.serverLevel();
                BlockPos controllerPos = packet.controller;
                if (newPos != null){
                    // 方块有效性检验
                    if (level.getBlockEntity(newPos) instanceof IBioCamera &&
                            level.getBlockEntity(controllerPos) instanceof IBioController &&
                            // 连通性检验
                            BioNetHelper.isConnected(level, newPos, controllerPos)
                    ) {
                        // 尝试占用摄像机
                        Player player = BioCameraManager.get(level).tryOccupyCamera(newPos, sender);
                        if (sender.equals(player)){
                            // 成功占用 解除原有占用
                            BioCameraManager.get(level).releaseCamera(oldPos);
                            // 回复使用者的UUID
                            BioMachineryNetwork.INSTANCE.send(
                                    PacketDistributor.PLAYER.with(() -> sender),
                                    new Response(ResultState.SUCCESS, newPos, player.getUUID())
                            );
                            // 广播更新
                            broadcastUpdate(level, newPos);
                        } else {
                            // 目标摄像机已经被占用，回复占用者的UUID
                            BioMachineryNetwork.INSTANCE.send(
                                    PacketDistributor.PLAYER.with(() -> sender),
                                    new Response(ResultState.OCCUPIED, newPos, player.getUUID())
                            );
                        }
                    } else {
                        // 检验不通过 返回不可用
                        BioMachineryNetwork.INSTANCE.send(
                                PacketDistributor.PLAYER.with(() -> sender),
                                new Response(ResultState.INVALID, newPos, null)
                        );
                    }
                } else {
                    // 目标是自我视角
                    BioMachineryNetwork.INSTANCE.send(
                            PacketDistributor.PLAYER.with(() -> sender),
                            new Response(ResultState.SUCCESS, null, sender.getUUID())
                    );
                    // 解除原有占用
                    BioCameraManager.get(level).releaseCamera(oldPos);
                    // 广播更新
                    broadcastUpdate(level, oldPos);
                }
            });
            context.setPacketHandled(true);
        }

        private static void broadcastUpdate(ServerLevel level, BlockPos cameraPos){
            Map<BlockPos, String> cameraOccupations = new HashMap<>();
            BioNetHelper
                    .findAllConnectedByConnector(level, cameraPos)
                    .forEach(machine -> {
                        if (!(machine instanceof IBioController controller) || !(controller.getUser() instanceof ServerPlayer serverPlayer)) return;
                        BioNetHelper.findAllConnectedByConnector(level, controller.getBlockPos())
                                .forEach(terminal -> {
                                    if (!(terminal instanceof IBioCamera camera)) return;
                                    CameraOccupation occupation = BioCameraManager.get(level).getCameraOwner(camera.getBlockPos());
                                    cameraOccupations.put(camera.getBlockPos(), occupation == null ? null : occupation.getPlayerName());
                                });
                        BioMachineryNetwork.INSTANCE.send(
                                PacketDistributor.PLAYER.with(() -> serverPlayer),
                                new UpdateBioCameraListPacket.Response(controller.getBlockPos(), cameraOccupations)
                        );
            });
        }
    }

    /**
     * 服务端回应客户端占用请求
     * @param resultState 回应状态：{@code SUCCESS | OCCUPIED | INVALID}
     * @param camera 实际控制的摄像机，{@code null} 表示为主视角（不占用任何摄像机）
     * @param userId 请求摄像机此刻占用的玩家 ID，{@code null} 表示摄像机不存在或者摄像机不可占用
     */
    public static record Response(ResultState resultState, @Nullable BlockPos camera, @Nullable UUID userId){
        public static void encode(Response packet, FriendlyByteBuf buf) {
            buf.writeEnum(packet.resultState);
            buf.writeNullable(packet.camera, FriendlyByteBuf::writeBlockPos);
            buf.writeNullable(packet.userId, FriendlyByteBuf::writeUUID);
        }

        public static Response decode(FriendlyByteBuf buf) {
            return new Response(
                    buf.readEnum(ResultState.class),
                    buf.readNullable(FriendlyByteBuf::readBlockPos),
                    buf.readNullable(FriendlyByteBuf::readUUID)
            );
        }

        public static void handle(Response packet, Supplier<NetworkEvent.Context> contextSupplier) {
            NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> {
                LocalPlayer player = Minecraft.getInstance().player;
                if (player == null || !(player.containerMenu instanceof BioControllerMenu menu)) return;
                BlockPos pos = packet.camera;
                switch (packet.resultState){
                    case SUCCESS -> {
                        // 成功占用. 理论上返回的玩家 ID 与客户端玩家 ID 一致
                        if (player.getUUID().equals(packet.userId)){
                            // 剩下交由 Menu 内部验证
                            BioMachineryNetwork.LOGGER.debug(
                                    "Occupy bio-camera successfully at " + (pos == null ? "self" : pos.toShortString())
                            );
                            menu.respondSelecting(ResultState.SUCCESS, pos);
                        }
                        else {
                            // 当作 OCCUPIED 处理
                            BioMachineryNetwork.LOGGER.warn(
                                    "Unexpected packet in responding to occupying bio-camera: " +
                                            "responding with SUCCESS state attaching different player UUID from sender"
                            );
                            if (pos == null) {
                                BioMachineryNetwork.LOGGER.warn(
                                        "Unexpected packet in responding to occupying bio-camera: " +
                                                "responding with OCCUPIED state attaching NULL camera position"
                                );
                            } else {
                                menu.respondSelecting(ResultState.OCCUPIED, pos);
                            }
                        }
                    }
                    case OCCUPIED -> {
                        // 已被占用
                        if (pos == null) {
                            BioMachineryNetwork.LOGGER.warn(
                                    "Unexpected packet in responding to occupying bio-camera: " +
                                            "responding with OCCUPIED state attaching NULL camera position"
                            );
                        } else {
                            menu.respondSelecting(ResultState.OCCUPIED, pos);
                        }
                    }
                    case INVALID -> {
                        // 不可用
                        if (packet.userId != null) BioMachineryNetwork.LOGGER.warn(
                                "Unexpected packet in responding to occupying bio-camera: " +
                                        "responding with INVALID state attaching player UUID"
                        );
                        if (pos == null) {
                            BioMachineryNetwork.LOGGER.warn(
                                    "Unexpected packet in responding to occupying bio-camera: " +
                                            "responding with OCCUPIED state attaching NULL camera position"
                            );
                        } else {
                            menu.respondSelecting(ResultState.INVALID, pos);
                        }
                    }
                }
            });
            context.setPacketHandled(true);
        }
    }
}
