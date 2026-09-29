package campionebase.erosionera.network.packet.s2c;

import campionebase.erosionera.api.BioMachineUpdate;
import campionebase.erosionera.inventory.BioControllerMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public record BioMachineUpdatePacket(List<BioMachineUpdate> updates) {
    public static void encode(BioMachineUpdatePacket packet, FriendlyByteBuf buf){
        buf.writeCollection(packet.updates, BioMachineUpdate::save);
    }

    public static BioMachineUpdatePacket decode(FriendlyByteBuf buf){
        return new BioMachineUpdatePacket(
                buf.readCollection(ArrayList::new, BioMachineUpdate::load)
        );
    }

    public static void handle(BioMachineUpdatePacket packet, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) return;
            if (player.containerMenu instanceof BioControllerMenu menu){
                menu.enqueueUpdates(packet.updates);
            }
        });
        context.setPacketHandled(true);
    }
}
