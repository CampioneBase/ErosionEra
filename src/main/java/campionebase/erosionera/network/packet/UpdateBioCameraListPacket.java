package campionebase.erosionera.network.packet;

import campionebase.erosionera.api.IBioCamera;
import campionebase.erosionera.api.IBioMachine;
import campionebase.erosionera.inventory.BioControllerMenu;
import campionebase.erosionera.network.BioMachineryNetwork;
import campionebase.erosionera.network.BioNetHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
                if (!(player.containerMenu instanceof BioControllerMenu menu)) return;
                if (!menu.getBlockPos().equals(packet.controller)) return;

                Set<BlockPos> cameras = BioNetHelper
                        .findAllConnectedByConnector(player.serverLevel(), packet.controller)
                        .stream()
                        .filter(machine -> machine instanceof IBioCamera)
                        .map(IBioMachine::getBlockPos)
                        .collect(Collectors.toSet());

                BioMachineryNetwork.INSTANCE.send(
                        PacketDistributor.PLAYER.with(() -> player),
                        new Response(packet.controller, cameras));
            });
            context.setPacketHandled(true);
        }
    }

    public static record Response(BlockPos controllerPos, Set<BlockPos> cameras){
        public static void encode(Response packet, FriendlyByteBuf buf) {
            buf.writeBlockPos(packet.controllerPos);
            buf.writeCollection(packet.cameras, FriendlyByteBuf::writeBlockPos);
        }

        public static Response decode(FriendlyByteBuf buf) {
            BlockPos pos = buf.readBlockPos();
            Set<BlockPos> cameras = buf.readCollection(HashSet::new, FriendlyByteBuf::readBlockPos);
            return new Response(pos, cameras);
        }

        public static void handle(Response packet, Supplier<NetworkEvent.Context> contextSupplier) {
            NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> {
                Player player = Minecraft.getInstance().player;
                if (player != null && player.containerMenu instanceof BioControllerMenu menu){
                    if (menu.getBlockPos().equals(packet.controllerPos)) {
                        menu.updateCameraList(packet.cameras);
                    }
                }
            });
            context.setPacketHandled(true);
        }
    }
}
