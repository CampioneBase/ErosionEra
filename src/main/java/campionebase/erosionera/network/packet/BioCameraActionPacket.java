package campionebase.erosionera.network.packet;

import campionebase.erosionera.api.IBioCamera;
import campionebase.erosionera.api.IBioControllable;
import campionebase.erosionera.inventory.BioControllerMenu;
import campionebase.erosionera.network.BioCameraHelper;
import campionebase.erosionera.network.BioCameraManager;
import campionebase.erosionera.network.BioMachineryService;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record BioCameraActionPacket(BlockPos camera, IBioControllable.ControlAction action, float yaw, float pitch) {
    public static void encode(BioCameraActionPacket packet, FriendlyByteBuf buf){
        buf.writeBlockPos(packet.camera);
        buf.writeEnum(packet.action);
        buf.writeFloat(packet.yaw);
        buf.writeFloat(packet.pitch);
    }

    public static BioCameraActionPacket decode(FriendlyByteBuf buf){
        return new BioCameraActionPacket(
                buf.readBlockPos(),
                buf.readEnum(IBioControllable.ControlAction.class),
                buf.readFloat(),
                buf.readFloat()
        );
    }

    public static void handle(BioCameraActionPacket packet, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer sender = context.getSender();
            if (sender == null) return;
            ServerLevel level = sender.serverLevel();
            // 检测使用者
            if (!(sender.containerMenu instanceof BioControllerMenu)) return;
            if (!(level.getBlockEntity(packet.camera) instanceof IBioCamera)) return;
            if (!BioCameraManager.get(level).tryRenewCamera(packet.camera, sender.getUUID(), packet.yaw, packet.pitch)) return;

            BlockHitResult result = BioCameraHelper.pickBlock(level, packet.camera, packet.yaw, packet.pitch);
            if (result.getType() == HitResult.Type.MISS) return;
            // 检查目标是否与摄像机连通
            if (!BioMachineryService.isConnected(level, packet.camera, result.getBlockPos())) return;
            if (level.getBlockEntity(result.getBlockPos()) instanceof IBioControllable target) {
                target.onControlledAction(sender, packet.action);
            }
        });
        context.setPacketHandled(true);
    }
}
