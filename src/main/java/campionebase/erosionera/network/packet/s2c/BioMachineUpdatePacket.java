package campionebase.erosionera.network.packet.s2c;

import campionebase.erosionera.api.BioMachineData;
import campionebase.erosionera.inventory.BioControllerMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record BioMachineUpdatePacket(BioMachineData data) {
    public static void encode(BioMachineUpdatePacket packet, FriendlyByteBuf buf){
        BioMachineData.save(buf, packet.data);
    }

    public static BioMachineUpdatePacket decode(FriendlyByteBuf buf){
        return new BioMachineUpdatePacket(BioMachineData.load(buf));
    }

    public static void handle(BioMachineUpdatePacket packet, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) return;
            if (player.containerMenu instanceof BioControllerMenu menu){
                menu.enqueueMessage(packet.data);
            }
        });
        context.setPacketHandled(true);
    }
}
