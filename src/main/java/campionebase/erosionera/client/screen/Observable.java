package campionebase.erosionera.client.screen;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Observable {
    @NotNull List<Component> getInfo();

    interface Item {
        @NotNull List<Component> getInfo(@NotNull ItemStack itemStack);
    }
}