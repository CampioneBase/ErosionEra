package campionebase.erosionera.client.event;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Observable<S> {
    @NotNull List<Component> getInfo(@NotNull Player player, S source);

    interface ItemSource extends Observable<ItemStack>{}

    interface BlockSource extends Observable<BlockState>{}

    interface BlockEntitySource extends Observable<BlockEntity>{}

    interface EntitySource extends Observable<Entity>{}
}