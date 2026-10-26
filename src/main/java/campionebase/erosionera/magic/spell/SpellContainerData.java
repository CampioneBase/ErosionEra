package campionebase.erosionera.magic.spell;

import campionebase.erosionera.magic.spell.module.SpellPayload;
import campionebase.erosionera.magic.spell.module.SpellPayloads;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * 魔法石（容器）在物品 NBT 中记录"一个魔法"的格式。
 *
 * <pre>
 * {
 *   "magic_type": "erosionera:rune",     // 魔法类型（载荷类型判别）
 *   "magic_data": { ... }               // 该类型自己的数据
 * }
 * </pre>
 *
 * <p>魔法石的核心含义就是**存储一个魔法（类型 + 数据）**，因此键名用 magic_ 前缀；
 * 容器只认这两个键，具体类型的解释交给 {@link SpellPayloads}。
 */
final class SpellContainerData {

    static final String TAG_MAGIC_TYPE = "magic_type";
    static final String TAG_MAGIC_DATA = "magic_data";

    private SpellContainerData() {
        throw new UnsupportedOperationException();
    }

    @Nullable
    static SpellPayload get(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(TAG_MAGIC_TYPE)) {
            return null;
        }
        ResourceLocation type = ResourceLocation.tryParse(tag.getString(TAG_MAGIC_TYPE));
        if (type == null) {
            return null;
        }
        return SpellPayloads.load(type, tag.getCompound(TAG_MAGIC_DATA));
    }

    static void set(ItemStack stack, SpellPayload payload) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString(TAG_MAGIC_TYPE, payload.type().toString());
        tag.put(TAG_MAGIC_DATA, payload.save());
    }

    @Nullable
    static SpellPayload remove(ItemStack stack) {
        SpellPayload payload = get(stack);
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            tag.remove(TAG_MAGIC_TYPE);
            tag.remove(TAG_MAGIC_DATA);
        }
        return payload;
    }
}
