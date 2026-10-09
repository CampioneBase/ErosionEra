package campionebase.erosionera.api;

import campionebase.erosionera.registry.BioMachineTypes;
import org.jetbrains.annotations.NotNull;

public interface IBioNutritionTank extends IBioMachine {
    @Override
    default @NotNull BioMachineType<? extends IBioMachine> getMachineType(){
        return BioMachineTypes.NUTRITION_TANK.get();
    }
}
