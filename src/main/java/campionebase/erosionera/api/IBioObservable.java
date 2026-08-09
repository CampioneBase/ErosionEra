package campionebase.erosionera.api;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public interface IBioObservable<S> {

    @NotNull List<Component> getInfo(S source);

    interface BlockSource extends IBioObservable<BlockState>{}

    interface BlockEntitySource extends IBioObservable<BlockEntity>{
        default @NotNull List<Component> getAllInfo(BlockEntity source) {
            List<Component> result = new LinkedList<>(this.getInfo(source));
            BlockState blockState = source.getBlockState();
            if (blockState.getBlock() instanceof BlockSource block){
                result.addAll(block.getInfo(blockState));
            }
            return result;
        }
    }

    interface EntitySource extends IBioObservable<Entity> {}
}
