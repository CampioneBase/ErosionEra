package campionebase.erosionera.magic.spell;

import campionebase.erosionera.magic.spell.module.SpellPayload;
import campionebase.erosionera.magic.spell.module.SpellPayloads;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * 容器载荷在物品 NBT 中的存放格式。
 *
 * <pre>
 * {
 *   "module_type": "erosionera:runes",   // 载荷类型判别
 *   "module_data": { ... }               // 载荷自身数据
 * }
 * </pre>
 */
final class SpellContainerData {

    static final String TAG_MODULE_TYPE = "module_type";
    static final String TAG_MODULE_DATA = "module_data";

    private SpellContainerData() {
        throw new UnsupportedOperationException();
    }

    @Nullable
    static SpellPayload get(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(TAG_MODULE_TYPE)) {
            return null;
        }
        ResourceLocation type = ResourceLocation.tryParse(tag.getString(TAG_MODULE_TYPE));
        if (type == null) {
            return null;
        }
        return SpellPayloads.load(type, tag.getCompound(TAG_MODULE_DATA));
    }

    static void set(ItemStack stack, SpellPayload payload) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString(TAG_MODULE_TYPE, payload.type().toString());
        tag.put(TAG_MODULE_DATA, payload.save());
    }

    @Nullable
    static SpellPayload remove(ItemStack stack) {
        SpellPayload payload = get(stack);
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            tag.remove(TAG_MODULE_TYPE);
            tag.remove(TAG_MODULE_DATA);
        }
        return payload;
    }
}
