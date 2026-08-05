package campionebase.erosionera.network.packet;

import campionebase.erosionera.blockentity.BioCameraBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record BioCameraNamingPacket(BlockPos pos, String name) {

    public static void encode(BioCameraNamingPacket packet, FriendlyByteBuf buf) {
        buf.writeBlockPos(packet.pos);
        buf.writeUtf(packet.name);
    }

    public static BioCameraNamingPacket decode(FriendlyByteBuf buf) {
        return new BioCameraNamingPacket(
                buf.readBlockPos(),
                buf.readUtf()
        );
    }

    public static void handle(BioCameraNamingPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer sender = context.getSender();
            if (sender == null) return;
            ServerLevel level = sender.serverLevel();
            if (level.getBlockEntity(packet.pos) instanceof BioCameraBlockEntity camera){
                camera.setName(packet.name);
            }
        });
        context.setPacketHandled(true);
    }
}
