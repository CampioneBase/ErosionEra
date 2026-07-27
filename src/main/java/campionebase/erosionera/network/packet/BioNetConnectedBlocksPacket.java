package campionebase.erosionera.network.packet;

import campionebase.erosionera.block.entity.BioControllerBlockEntity;
import campionebase.erosionera.network.BioMachineryNetwork;
import campionebase.erosionera.network.BioNetData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class BioNetConnectedBlocksPacket {

    public static record Request(BlockPos pos){
        public static void encode(Request packet, FriendlyByteBuf buf) {
            buf.writeBlockPos(packet.pos);
        }

        public static Request decode(FriendlyByteBuf buf) {
            return new Request(buf.readBlockPos());
        }

        public static void handle(Request packet, Supplier<NetworkEvent.Context> contextSupplier) {
            NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() ->{
                ServerPlayer sender = context.getSender();
                if (sender == null) return;
                Set<BlockPos> connected = BioNetData.get(sender.serverLevel()).getAllConnectedBlocks(packet.pos);
                BioMachineryNetwork.INSTANCE.send(
                        PacketDistributor.PLAYER.with(() -> sender),
                        new Response(packet.pos, connected)
                );
            });
            context.setPacketHandled(true);
        }
    }

    public static record Response(BlockPos source, Set<BlockPos> connected){
        public static void encode(Response packet, FriendlyByteBuf buf) {
            buf.writeBlockPos(packet.source);
            buf.writeCollection(packet.connected, FriendlyByteBuf::writeBlockPos);
        }

        public static Response decode(FriendlyByteBuf buf) {
            BlockPos source = buf.readBlockPos();
            Set<BlockPos> neighbors = buf.readCollection(ConcurrentHashMap::newKeySet, FriendlyByteBuf::readBlockPos);
            return new Response(source, neighbors);
        }

        public static void handle(Response packet, Supplier<NetworkEvent.Context> contextSupplier) {
            NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() ->{
                ServerPlayer sender = context.getSender();
                if (sender == null) return;
                if (sender.level().getBlockEntity(packet.source) instanceof BioControllerBlockEntity core){

                }
            });
            context.setPacketHandled(true);
        }
    }
}
