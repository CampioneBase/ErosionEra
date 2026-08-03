package campionebase.erosionera.network.packet;

import campionebase.erosionera.api.IBioCamera;
import campionebase.erosionera.api.IBioController;
import campionebase.erosionera.inventory.BioControllerMenu;
import campionebase.erosionera.network.BioCameraManager;
import campionebase.erosionera.network.BioMachineryNetwork;
import campionebase.erosionera.network.BioNetHelper;
import campionebase.erosionera.network.CameraOccupation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class UpdateBioCameraListPacket{

    public static record Request(BlockPos controller){
        public static void encode(Request packet, FriendlyByteBuf buf) {
            buf.writeBlockPos(packet.controller);
        }

        public static Request decode(FriendlyByteBuf buf) {
            return new Request(buf.readBlockPos());
        }

        public static void handle(Request packet, Supplier<NetworkEvent.Context> contextSupplier) {
            NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> {
                ServerPlayer player = context.getSender();
                if (player == null) return;
                ServerLevel level = player.serverLevel();
                if (!(player.containerMenu instanceof BioControllerMenu menu)) return;
                if (!menu.getBlockPos().equals(packet.controller)) return;
                Map<BlockPos, String> cameraOccupations = new HashMap<>();
                BioNetHelper
                        .findAllConnectedByConnector(player.serverLevel(), packet.controller)
                        .stream()
                        .filter(machine -> machine instanceof IBioCamera)
                        .forEach(machine -> {
                            CameraOccupation controller = BioCameraManager.get(level).getCameraOwner(machine.getBlockPos());
                            cameraOccupations.put(machine.getBlockPos(), controller == null ? null : controller.getPlayerName());
                        });

                BioMachineryNetwork.INSTANCE.send(
                        PacketDistributor.PLAYER.with(() -> player),
                        new Response(packet.controller, cameraOccupations));
            });
            context.setPacketHandled(true);
        }
    }

    public static record Response(BlockPos controllerPos, Map<BlockPos, @Nullable String> cameraOccupations){
        public static void encode(Response packet, FriendlyByteBuf buf) {
            buf.writeBlockPos(packet.controllerPos);
            buf.writeMap(
                    packet.cameraOccupations,
                    FriendlyByteBuf::writeBlockPos,
                    (buf1, pos) -> buf1.writeNullable(pos, FriendlyByteBuf::writeUtf)
            );
        }

        public static Response decode(FriendlyByteBuf buf) {
            return new Response(
                    buf.readBlockPos(),
                    buf.readMap(
                            FriendlyByteBuf::readBlockPos,
                            buf1 -> buf1.readNullable(FriendlyByteBuf::readUtf)
                    )
            );
        }

        public static void handle(Response packet, Supplier<NetworkEvent.Context> contextSupplier) {
            NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> {
                LocalPlayer player = Minecraft.getInstance().player;
                if (player != null && player.containerMenu instanceof BioControllerMenu menu){
                    ClientLevel level = player.clientLevel;
                    if (menu.getBlockPos().equals(packet.controllerPos)) {
                        Map<IBioCamera, String> result = new HashMap<>();
                        // 事实检验过滤
                        packet.cameraOccupations.forEach((k, v) -> {
                            // 检验摄像机 - 客户端找不到摄像机就直接过滤
                            if (!(level.getBlockEntity(k) instanceof IBioCamera camera)) return;
                            result.put(camera, v);
                        });
                        menu.refreshCameraOccupation(result);
                    }
                }
            });
            context.setPacketHandled(true);
        }
    }
}
