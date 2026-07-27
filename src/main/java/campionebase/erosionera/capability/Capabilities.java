package campionebase.erosionera.capability;

import campionebase.erosionera.capability.nutrition.INutritionStorage;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class Capabilities {
    public static final Capability<INutritionStorage> NUTRITION = CapabilityManager.get(new CapabilityToken<>() {});
}
