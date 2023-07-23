package lunastic.erosion_era.block.util;

import lunastic.erosion_era.init.Properties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;

import java.util.function.ToIntFunction;

public interface Erodible {

    IntProperty ENERGY = Properties.EROSION_ENERGY;

    int ENERGY_MAX = 1000;

    static ToIntFunction<BlockState> getLuminanceFunctionWithEE(int maxLuminance){
        return state -> {
            if (state.get(ENERGY) == null) return 0;
            int energy = state.get(ENERGY);
            // ~0% -> 0%,
            if (energy <= 0) return 0;
            // (0%, 33.3%) -> 25%,
            if (energy < ENERGY_MAX / 3) return maxLuminance / 4;
            // [33.3%, 66.7%) -> 50%,
            if (energy < ENERGY_MAX * 2 / 3) return maxLuminance / 2;
            // [66.7%, 100%) -> 75%,
            if (energy < ENERGY_MAX) return maxLuminance * 3 / 4;
            // 100% -> 100%
            return maxLuminance;
        };
    }

    default void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ENERGY);
    }
}
