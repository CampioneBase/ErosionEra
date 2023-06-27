package lunastic.erosion_era.init;

import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;

public class ErErProperties {
    public static final BooleanProperty ORIGINAL = BooleanProperty.of("original");

    /** 侵蚀能量（0 ~ 1000）   */
    public static final IntProperty EROSION_ENERGY = IntProperty.of("erosion_energy", 0, 1000);
}
