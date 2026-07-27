package campionebase.erosionera.capability.nutrition;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

/**
 * 营养值
 */
@AutoRegisterCapability
public interface INutritionStorage {

    /**
     * 通过喂食获取营养值
     */
    void feed(ItemStack itemStack);

    int consume(int consume);
}
