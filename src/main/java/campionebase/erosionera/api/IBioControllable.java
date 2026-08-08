package campionebase.erosionera.api;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public interface IBioControllable {
    enum ControlAction{
        INCREMENT,
        DECREMENT,
        ATTACK,
        USE
    }

    void onControlledAction(ServerPlayer player, ControlAction action);
}
