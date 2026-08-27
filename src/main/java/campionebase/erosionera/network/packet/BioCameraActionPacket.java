package campionebase.erosionera.network.packet;

import campionebase.erosionera.api.IBioCamera;
import campionebase.erosionera.api.IBioControllable;
import campionebase.erosionera.api.IBioController;
import campionebase.erosionera.inventory.BioControllerMenu;
import campionebase.erosionera.network.BioCameraHelper;
import campionebase.erosionera.network.BioCameraManager;
import campionebase.erosionera.network.BioMachineryNetwork;
import campionebase.erosionera.network.BioMachineryService;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record BioCameraActionPacket(BlockPos camera, IBioController.Action action, float yaw, float pitch) {
    public static void encode(BioCameraActionPacket packet, FriendlyByteBuf buf){
        buf.writeBlockPos(packet.camera);
        buf.writeEnum(packet.action);
        buf.writeFloat(packet.yaw);
        buf.writeFloat(packet.pitch);
    }

    public static BioCameraActionPacket decode(FriendlyByteBuf buf){
        return new BioCameraActionPacket(
                buf.readBlockPos(),
                buf.readEnum(IBioController.Action.class),
                buf.readFloat(),
                buf.readFloat()
        );
    }

    public static void handle(BioCameraActionPacket packet, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        ServerPlayer sender = context.getSender();
        if (sender == null) return;
        context.enqueueWork(() -> {
            ServerLevel level = sender.serverLevel();
            // 检测使用者
            if (!(sender.containerMenu instanceof BioControllerMenu) ||
                    !(level.getBlockEntity(packet.camera) instanceof IBioCamera camera) ||
                    !BioCameraManager.get(level).tryRenewCamera(packet.camera, sender.getUUID(), packet.yaw, packet.pitch)
            ) {
                BioMachineryNetwork.LOGGER.warn(
                        "Preventing {} from doing bio-camera[{}] action[{}]: bio-camera invalid.",
                        sender.getName().getString(), packet.camera.toShortString(), packet.action
                );
                return;
            }

            BlockHitResult result = BioCameraHelper.pickBlock(level, camera, packet.yaw, packet.pitch);
            // 检测目标是否可操作
            if (result.getType() == HitResult.Type.MISS) return;
            // 检查目标是否与摄像机的控制器连通
            BioCameraManager.CameraOccupation occupation = BioCameraManager.get(level).getCameraOwner(packet.camera);
            if (occupation == null || !occupation.getPlayerUUID().equals(sender.getUUID())) {
                BioMachineryNetwork.LOGGER.warn(
                        "Preventing {} from doing bio-camera[{}] action[{}]: " +
                                "bio-camera is not used or player is not current user.",
                        sender.getName().getString(), packet.camera.toShortString(), packet.action
                );
                return;
            }
            if (!(level.getBlockEntity(occupation.getController()) instanceof IBioController controller)) {
                BioMachineryNetwork.LOGGER.warn(
                        "Preventing {} from doing bio-camera[{}] action[{}]: " +
                                "bio-camera disconnected or bio-controller invalid.",
                        sender.getName().getString(), packet.camera.toShortString(), packet.action
                );
                return;
            }
            // 检查控制器是否与目标连通，目标无效属于正常流程
            if (!BioMachineryService.isConnected(level, controller.getBlockPos(), result.getBlockPos())) return;
            if (level.getBlockEntity(result.getBlockPos()) instanceof IBioControllable target) {
                controller.control(target, packet.action);
            }
        });
        context.setPacketHandled(true);
    }
}
