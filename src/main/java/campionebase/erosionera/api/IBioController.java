package campionebase.erosionera.api;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface IBioController extends IBioMachine {
    @Override
    default boolean isCore() {
        return true;
    }
    @Nullable
    Player getUser();
}
