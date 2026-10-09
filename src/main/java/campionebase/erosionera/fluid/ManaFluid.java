package campionebase.erosionera.fluid;

import campionebase.erosionera.registry.ErErFluids;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import org.jetbrains.annotations.NotNull;

public abstract class ManaFluid extends ForgeFlowingFluid {
    protected ManaFluid() {
        super(new Properties(
                ErErFluids.MANA_TYPE,
                ErErFluids.MANA_SOURCE,
                ErErFluids.MANA_FLOWING)
                .block(ErErFluids.MANA_BLOCK)
                .bucket(ErErFluids.MANA_BUCKET)
        );
    }

    @Override
    public Fluid getSource() {
        return ErErFluids.MANA_SOURCE.get();
    }

    @Override
    public Fluid getFlowing() {
        return ErErFluids.MANA_FLOWING.get();
    }

    public static class Source extends ManaFluid {

        @Override
        public boolean isSource(@NotNull FluidState fluidState) {
            return true;
        }

        @Override
        public int getAmount(@NotNull FluidState fluidState) {
            return 8;
        }
    }

    public static class Flowing extends ManaFluid {

        public Flowing(){
            super();
            registerDefaultState(getStateDefinition().any().setValue(LEVEL, 7));
        }

        @Override
        protected void createFluidStateDefinition(StateDefinition.@NotNull Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public boolean isSource(@NotNull FluidState fluidState) {
            return false;
        }

        @Override
        public int getAmount(@NotNull FluidState fluidState) {
            return fluidState.getValue(LEVEL);
        }
    }
}
