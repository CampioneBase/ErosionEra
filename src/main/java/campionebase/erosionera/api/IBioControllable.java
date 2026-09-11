package campionebase.erosionera.api;

import net.minecraft.server.level.ServerPlayer;

public interface IBioControllable {

    void onControlledAction(ServerPlayer player, IBioController.Action action);
}
