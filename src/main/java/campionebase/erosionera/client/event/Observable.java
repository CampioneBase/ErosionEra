package campionebase.erosionera.client.event;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Observable {
    @NotNull List<Component> getInfo(@NotNull Player player);

    interface Item {
        @NotNull List<Component> getInfo(@NotNull Player player, @NotNull ItemStack itemStack);
    }
}