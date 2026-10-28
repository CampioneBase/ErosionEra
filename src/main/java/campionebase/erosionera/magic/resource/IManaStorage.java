package campionebase.erosionera.magic.resource;

import net.minecraft.nbt.IntTag;
import net.minecraft.util.Mth;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.INBTSerializable;

@AutoRegisterCapability
public interface IManaStorage extends IManaPool, INBTSerializable<IntTag> {
    Capability<IManaStorage> CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    String TAG_MANA_STORAGE = "mana_storage";
    /** 魔力容量 */
    int capacity();

    /**
     * 补充魔力
     * @return 返回实际补充到的魔力
     */
    int receive(int amount, boolean simulate);

    @Override
    default int extract(int amount) {
        return this.extract(amount, false);
    }

    int extract(int amount, boolean simulate);

    static int transfer(IManaStorage from, IManaStorage to, int amount, boolean simulate){
        int output = from.extract(amount, true);
        int input = to.receive(amount, true);
        int value = Math.min(input, output);
        if (!simulate){
            from.extract(value, false);
            to.receive(value, false);
        }
        return value;
    }
}
