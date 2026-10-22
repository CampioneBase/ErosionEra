package campionebase.erosionera.magic.spell;

import campionebase.erosionera.magic.spell.logic.IRuneLogic;
import campionebase.erosionera.magic.spell.logic.LinearRuneLogic;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * 符文序列：**不可变数据**（有序的符文 id 列表）。
 *
 * <p>遍历游标不在这里——那属于 {@link IRuneLogic} 的运行时状态。
 * 两者分离的原因：序列需要在配方、NBT、载荷间自由共享，而游标必须每颗子弹一份，
 * 混在一起会造成共享可变状态（尤其 {@link #EMPTY} 这种全局实例）。
 */
public final class RuneSequence {

    public static final String TAG_RUNES = RuneUnits.TAG_RUNES;
    public static final RuneSequence EMPTY = new RuneSequence(List.of());

    private final List<ResourceLocation> ids;

    public RuneSequence(@NotNull List<ResourceLocation> ids) {
        Objects.requireNonNull(ids);
        // 防御性拷贝：外部修改不应穿透到序列
        this.ids = List.copyOf(ids);
    }

    /** 从物品 NBT 提取符文序列（非法 id 被跳过）。 */
    @NotNull
    public static RuneSequence fromItemStack(ItemStack itemStack) {
        return RuneUnits.read(itemStack);
    }

    /** 创建一份**新的**线性遍历逻辑（每颗子弹一份）。 */
    public IRuneLogic newLogic() {
        return new LinearRuneLogic(this);
    }

    /** 符文法力总和。 */
    public int totalMana() {
        int sum = 0;
        for (ResourceLocation id : this.ids) {
            RuneEntry entry = RuneEntry.get(id);
            if (entry != null) {
                sum += entry.mana();
            }
        }
        return sum;
    }

    /** 按绝对下标取符文 id；越界返回 {@code null}。 */
    @Nullable
    public ResourceLocation get(int index) {
        if (index < 0 || index >= this.ids.size()) {
            return null;
        }
        return this.ids.get(index);
    }

    public boolean isEmpty() {
        return this.ids.isEmpty();
    }

    public int size() {
        return this.ids.size();
    }

    public List<ResourceLocation> toList() {
        return this.ids;
    }

    public void forEach(Consumer<ResourceLocation> consumer) {
        this.ids.forEach(consumer);
    }

    @Override
    public String toString() {
        return "RuneSequence{ids=" + this.ids + '}';
    }
}
