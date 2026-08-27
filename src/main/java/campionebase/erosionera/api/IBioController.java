package campionebase.erosionera.api;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public interface IBioController extends IBioMachine {
    enum Action{
        INCREMENT,
        DECREMENT,
        ATTACK,
        USE,
        MARK
    }


    @Override
    default boolean isCore() {
        return true;
    }
    @Nullable
    Player getUser();

    void onReleased();

    void control(IBioControllable target, Action action);
}
