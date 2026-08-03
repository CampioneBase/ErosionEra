package campionebase.erosionera.network.packet;

import campionebase.erosionera.api.IBioCamera;
import campionebase.erosionera.api.IBioController;
import campionebase.erosionera.inventory.BioControllerMenu;
import campionebase.erosionera.network.BioCameraManager;
import campionebase.erosionera.network.BioMachineryNetwork;
import campionebase.erosionera.network.BioNetData;
import campionebase.erosionera.network.BioNetHelper;
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

import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

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
                    if (level.getBlockEntity(newPos) instanceof IBioCamera camera &&
                            level.getBlockEntity(controllerPos) instanceof IBioController controller &&
                            // 连通性检验
                            BioNetHelper.isConnected(level, newPos, controllerPos)
                    ) {
                        // 尝试占用摄像机
                        Player player = BioCameraManager.get(level).tryOccupyCamera(newPos, sender);
                        if (sender.equals(player)){
                            // 成功占用 回复使用者的UUID
                            BioMachineryNetwork.INSTANCE.send(
                                    PacketDistributor.PLAYER.with(() -> sender),
                                    new Response(ResultState.SUCCESS, newPos, player.getUUID())
                            );
                            // 向同网络内其他玩家广播

                        } else {
                            // 目标摄像机已经被占用，回复占用者的UUID
                            BioMachineryNetwork.INSTANCE.send(
                                    PacketDistributor.PLAYER.with(() -> sender),
                                    new Response(ResultState.OCCUPIED, oldPos, player.getUUID())
                            );
                        }
                    } else {
                        // 检验不通过 返回不可用
                        BioMachineryNetwork.INSTANCE.send(
                                PacketDistributor.PLAYER.with(() -> sender),
                                new Response(ResultState.INVALID, oldPos, null)
                        );
                    }
                } else {
                    // 目标是自我视角
                    BioMachineryNetwork.INSTANCE.send(
                            PacketDistributor.PLAYER.with(() -> sender),
                            new Response(ResultState.SUCCESS, null, sender.getUUID())
                    );
                        BioCameraManager.get(level).releaseCamera(oldPos);
                }
            });
            context.setPacketHandled(true);
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
                if (player == null) return;
                switch (packet.resultState){
                    case SUCCESS -> {
                        // 成功占用. 理论上返回的玩家 ID 与客户端玩家 ID 一致
                        if (player.getUUID().equals(packet.userId)){
                            // 剩下交由 Menu 内部验证
                            if (player.containerMenu instanceof BioControllerMenu menu){
                                menu.respondSelecting(ResultState.SUCCESS, packet.camera);
                            }
                        }
                        else {
                            // 当作 OCCUPIED 处理
                            BioMachineryNetwork.LOGGER.warn(
                                    "Unexpected packet in responding to occupying bio-camera: " +
                                            "responding with SUCCESS state attaching different player UUID from sender"
                            );
                        }

                    }
                    case OCCUPIED -> {
                        // 已被占用
                    }
                    case INVALID -> {
                        // 不可用
                        if (packet.userId != null) BioMachineryNetwork.LOGGER.warn(
                                "Unexpected packet in responding to occupying bio-camera: " +
                                        "responding with INVALID state attaching player UUID"
                        );
                    }
                }
            });
            context.setPacketHandled(true);
        }
    }
}
