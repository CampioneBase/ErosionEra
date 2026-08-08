package campionebase.erosionera.network.packet;

import campionebase.erosionera.api.IBioCamera;
import campionebase.erosionera.network.BioCameraManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record BioCameraAlivePacket(BlockPos camera, float yaw, float pitch) {
    public static void encode(BioCameraAlivePacket packet, FriendlyByteBuf buf){
        buf.writeBlockPos(packet.camera);
        buf.writeFloat(packet.yaw);
        buf.writeFloat(packet.pitch);
    }

    public static BioCameraAlivePacket decode(FriendlyByteBuf buf){
        return new BioCameraAlivePacket(
                buf.readBlockPos(),
                buf.readFloat(),
                buf.readFloat()
        );
    }

    public static void handle(BioCameraAlivePacket packet, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer sender = context.getSender();
            if (sender == null) return;
            ServerLevel level = sender.serverLevel();

            if (!(level.getBlockEntity(packet.camera) instanceof IBioCamera)) return;

            BioCameraManager.get(level).tryRenewCamera(packet.camera, sender.getUUID(), packet.yaw, packet.pitch);
        });
        context.setPacketHandled(true);
    }
}
