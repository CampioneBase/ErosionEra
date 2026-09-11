package campionebase.erosionera.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IBioController {
    enum Action{
        INCREMENT,
        DECREMENT,
        ATTACK,
        USE,
        MARK
    }

    BlockPos getBlockPos();
    @Nullable
    Player getUser();
    @Nullable
    IBioCore getCore();
    @Nullable
    Entity getTarget();

    void setTarget(@Nullable Entity target);

    void onReleased();

    void control(IBioControllable target, Action action);
}
