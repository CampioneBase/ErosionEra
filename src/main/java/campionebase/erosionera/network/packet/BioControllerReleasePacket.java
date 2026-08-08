package campionebase.erosionera.network.packet;

import campionebase.erosionera.api.IBioController;
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
        context.enqueueWork(() ->{
            ServerPlayer sender = context.getSender();
            if (sender == null) return;
            ServerLevel level = sender.serverLevel();

            if (!(level.getBlockEntity(packet.controller) instanceof IBioController controller)) return;
            if (controller.getUser() == null) return;
            // 只能自己释放
            if (controller.getUser().getUUID().equals(sender.getUUID())) controller.onReleased();
        });
        context.setPacketHandled(true);
    }
}
