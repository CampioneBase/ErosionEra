package campionebase.erosionera.network.packet.c2s;

import campionebase.erosionera.blockentity.BioCameraBlockEntity;
import campionebase.erosionera.network.BioMachineryNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record BioCameraNamingPacket(BlockPos camera, String name) {

    public static void encode(BioCameraNamingPacket packet, FriendlyByteBuf buf) {
        buf.writeBlockPos(packet.camera);
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
        ServerPlayer sender = context.getSender();
        if (sender == null) return;
        context.enqueueWork(() -> {
            ServerLevel level = sender.serverLevel();
            if (level.getBlockEntity(packet.camera) instanceof BioCameraBlockEntity camera){
                camera.setName(packet.name);
            } else {
                BioMachineryNetwork.LOGGER.warn(
                        "Preventing {} from naming bio-camera[{}]: bio-camera invalid",
                        sender.getName().getString(), packet.camera
                );
            }
        });
        context.setPacketHandled(true);
    }
}
