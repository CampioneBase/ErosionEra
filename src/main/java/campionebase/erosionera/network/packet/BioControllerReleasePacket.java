package campionebase.erosionera.network.packet;

import campionebase.erosionera.api.IBioController;
import campionebase.erosionera.network.BioMachineryNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record BioControllerReleasePacket(BlockPos controller) {
    public static void encode(BioControllerReleasePacket packet, FriendlyByteBuf buf){
        buf.writeBlockPos(packet.controller);
    }

    public static BioControllerReleasePacket decode(FriendlyByteBuf buf){
        return new BioControllerReleasePacket(
                buf.readBlockPos()
        );
    }

    public static void handle(BioControllerReleasePacket packet, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        ServerPlayer sender = context.getSender();
        if (sender == null) return;
        context.enqueueWork(() ->{
            ServerLevel level = sender.serverLevel();

            if (!(level.getBlockEntity(packet.controller) instanceof IBioController controller)) {
                BioMachineryNetwork.LOGGER.warn(
                        "Preventing {} from releasing bio-controller[{}]: bio-controller invalid",
                        sender.getName().getString(), packet.controller.toShortString()
                );
                return;
            }
            if (controller.getUser() == null || !controller.getUser().getUUID().equals(sender.getUUID())) {
                // 只能自己释放
                BioMachineryNetwork.LOGGER.warn(
                        "Preventing {} from releasing bio-controller[{}]: user auth invalid",
                        sender.getName().getString(), packet.controller.toShortString()
                );
                return;
            }

            controller.onReleased();
        });
        context.setPacketHandled(true);
    }
}
