package campionebase.erosionera.api;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IBioControllable {

    @OnlyIn(Dist.DEDICATED_SERVER)
    void onControlledAction(ServerPlayer player, IBioController.Action action);
}
