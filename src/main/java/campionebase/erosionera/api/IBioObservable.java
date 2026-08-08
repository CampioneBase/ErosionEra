package campionebase.erosionera.api;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

public interface IBioObservable<S> {

    Component getInfo(S source);

    interface BlockSource extends IBioObservable<BlockState>{}
}
