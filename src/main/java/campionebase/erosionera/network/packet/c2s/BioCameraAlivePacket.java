package campionebase.erosionera.network.packet.c2s;

import campionebase.erosionera.network.BioCameraManager;
import campionebase.erosionera.network.BioMachineryService;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record BioCameraAlivePacket(BlockPos camera, BlockPos controller, float yaw, float pitch) {
    public static void encode(BioCameraAlivePacket packet, FriendlyByteBuf buf){
        buf.writeBlockPos(packet.camera);
        buf.writeBlockPos(packet.controller);
        buf.writeFloat(packet.yaw);
        buf.writeFloat(packet.pitch);
    }

    public static BioCameraAlivePacket decode(FriendlyByteBuf buf){
        return new BioCameraAlivePacket(
                buf.readBlockPos(),
                buf.readBlockPos(),
                buf.readFloat(),
                buf.readFloat()
        );
    }

    public static void handle(BioCameraAlivePacket packet, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        ServerPlayer sender = context.getSender();
        if (sender == null) return;
        context.enqueueWork(() -> {
            String failureText = "Failed to keep occupation alive";
            if (!BioMachineryService.authenticateCameraController(sender, packet.camera, packet.controller, failureText)) return;
            ServerLevel level = sender.serverLevel();
            BioCameraManager.get(level).tryRenewCamera(packet.camera, sender.getUUID(), packet.yaw, packet.pitch);
        });
        context.setPacketHandled(true);
    }
}
