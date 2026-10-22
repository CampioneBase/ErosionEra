package campionebase.erosionera.magic.spell;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * 符文序列的 NBT 读写工具。
 *
 * <p>持久化格式只有一种：{@link #TAG_RUNES} 下的**符文 id 字符串列表**。
 * 提供 {@link ItemStack} 与 {@link CompoundTag} 两套入口，后者供载荷（payload）复用。
 */
public final class RuneUnits {

    public static final String TAG_RUNES = "runes";
    /** 旧版单值键，仅用于读取兼容。 */
    public static final String TAG_ID = "rune_id";

    private RuneUnits() {
        throw new UnsupportedOperationException();
    }

    // ------------------------------------------------------------------ 写入

    /** 把符文序列写入 tag。空序列时移除该键，避免留下空列表。 */
    public static void write(CompoundTag tag, RuneSequence sequence) {
        if (sequence == null || sequence.isEmpty()) {
            tag.remove(TAG_RUNES);
            return;
        }
        ListTag listTag = new ListTag();
        sequence.forEach(id -> listTag.add(StringTag.valueOf(id.toString())));
        tag.put(TAG_RUNES, listTag);
    }

    /** 把符文序列写入物品 NBT。 */
    public static void white(ItemStack itemStack, RuneSequence sequence) {
        write(itemStack.getOrCreateTag(), sequence);
    }

    // ------------------------------------------------------------------ 读取

    /**
     * 从 tag 读取符文序列。
     *
     * <p>非法 id 会被**跳过**（{@code tryParse}），不会抛异常——NBT 可被玩家编辑，
     * 这里必须容错。
     */
    @NotNull
    public static RuneSequence read(@Nullable CompoundTag tag) {
        if (tag == null || !tag.contains(TAG_RUNES, Tag.TAG_LIST)) {
            return RuneSequence.EMPTY;
        }
        ListTag listTag = tag.getList(TAG_RUNES, Tag.TAG_STRING);
        List<ResourceLocation> ids = new ArrayList<>(listTag.size());
        for (int i = 0; i < listTag.size(); i++) {
            ResourceLocation id = ResourceLocation.tryParse(listTag.getString(i));
            if (id != null) {
                ids.add(id);
            }
        }
        return new RuneSequence(ids);
    }

    /** 从物品读取符文序列（不移除）。 */
    @NotNull
    public static RuneSequence read(ItemStack itemStack) {
        return read(itemStack.getTag());
    }

    /** 从 tag 读取并移除符文序列。 */
    @NotNull
    public static RuneSequence remove(@Nullable CompoundTag tag) {
        RuneSequence sequence = read(tag);
        if (tag != null) {
            tag.remove(TAG_RUNES);
        }
        return sequence;
    }

    /** 从物品读取并移除符文序列。 */
    @NotNull
    public static RuneSequence remove(ItemStack itemStack) {
        return remove(itemStack.getTag());
    }

    /** 物品是否拥有符文序列。 */
    public static boolean hasRuneSequence(ItemStack itemStack) {
        return !read(itemStack).isEmpty();
    }

    // ------------------------------------------------- 旧版单值（读取兼容）

    /** 写入单值符文 id（旧格式，保留兼容）。 */
    public static void setRune(ItemStack itemStack, ResourceLocation id) {
        itemStack.getOrCreateTag().putString(TAG_ID, id.toString());
    }

    /** 读取单值符文 id（旧格式，保留兼容）。 */
    @Nullable
    public static ResourceLocation getRune(ItemStack itemStack) {
        CompoundTag tag = itemStack.getTag();
        if (tag == null || !tag.contains(TAG_ID, Tag.TAG_STRING)) {
            return null;
        }
        return ResourceLocation.tryParse(tag.getString(TAG_ID));
    }

    /** 物品是否拥有单值符文 id（旧格式）。 */
    public static boolean hasRune(ItemStack itemStack) {
        return getRune(itemStack) != null;
    }

    // ------------------------------------------------------------------ 容器检索

    /** 从容器里选择第一个满足条件的物品；找不到返回 {@link ItemStack#EMPTY}。 */
    @NotNull
    public static <T extends Item> ItemStack find(Container container, Class<T> clazz, Predicate<ItemStack> condition) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack itemStack = container.getItem(i);
            if (clazz.isInstance(itemStack.getItem()) && condition.test(itemStack)) {
                return itemStack;
            }
        }
        return ItemStack.EMPTY;
    }
}
