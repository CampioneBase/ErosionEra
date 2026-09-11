package campionebase.erosionera.network.packet;

import campionebase.erosionera.api.BioMachineData;
import campionebase.erosionera.inventory.BioControllerMenu;
import campionebase.erosionera.network.BioMachineryNetwork;
import campionebase.erosionera.network.BioMachineryService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BioMachineListPacket {

    public static record Request(BlockPos core){
        public static void encode(Request packet, FriendlyByteBuf buf) {
            buf.writeBlockPos(packet.core);
        }

        public static Request decode(FriendlyByteBuf buf) {
            return new Request(buf.readBlockPos());
        }

        public static void handle(Request packet, Supplier<NetworkEvent.Context> contextSupplier) {
            NetworkEvent.Context context = contextSupplier.get();
            ServerPlayer sender = context.getSender();
            if (sender == null) return;
            context.enqueueWork(() -> {
                ServerLevel level = sender.serverLevel();
                if (!(sender.containerMenu instanceof BioControllerMenu menu)) return;
                if (menu.getCore() == null || !menu.getCore().getBlockPos().equals(packet.core)) return;

                Set<BioMachineData> dataSet = BioMachineryService.
                        findAllConnectedByConnector(level, packet.core)
                        .stream()
                        .map(BioMachineData::of)
                        .collect(Collectors.toSet());

                BioMachineryNetwork.INSTANCE.send(
                        PacketDistributor.PLAYER.with(() -> sender),
                        new Response(packet.core, dataSet));
            });
            context.setPacketHandled(true);
        }
    }

    public static record Response(BlockPos core, Set<BioMachineData> machineSet){
        public static void encode(Response packet, FriendlyByteBuf buf) {
            buf.writeBlockPos(packet.core);
            buf.writeCollection(packet.machineSet, BioMachineData::save);
        }

        public static Response decode(FriendlyByteBuf buf) {
            return new Response(
                    buf.readBlockPos(),
                    buf.readCollection(ConcurrentHashMap::newKeySet, BioMachineData::load)
            );
        }

        public static void handle(Response packet, Supplier<NetworkEvent.Context> contextSupplier) {
            NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> {
                LocalPlayer player = Minecraft.getInstance().player;
                if (player != null && player.containerMenu instanceof BioControllerMenu menu){
                    if (menu.getCore() == null || !menu.getCore().getBlockPos().equals(packet.core)) return;
                    menu.refreshData(packet.machineSet);
                }
            });
            context.setPacketHandled(true);
        }
    }
}
