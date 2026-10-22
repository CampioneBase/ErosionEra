package campionebase.erosionera.magic.spell.module;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * 「可装配模块」的物品契约：刻录石、Mantra 石等都实现本接口。
 *
 * <p>容器（{@link campionebase.erosionera.magic.spell.ISpellContainer}）只认
 * {@link SpellPayload}，不认具体模块物品；配方只认本接口，不认具体物品类。
 * 三者构成一条解耦链：
 *
 * <pre>
 *   配方 ──> ISpellModule（物品）──> SpellPayload（载荷）──> ISpellContainer（容器）
 * </pre>
 */
public interface ISpellModule {

    /** 本模块当前能否被安装（例如刻录石必须有符文、Mantra 石必须记录了 Mantra）。 */
    boolean canInstall(ItemStack moduleStack);

    /**
     * 提取要装进容器的载荷。
     *
     * <p>调用方应保证 {@link #canInstall(ItemStack)} 为真。
     */
    SpellPayload payload(ItemStack moduleStack);

    /** 从物品栈取模块；不是模块物品时返回 {@code null}。 */
    @Nullable
    static ISpellModule of(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        return stack.getItem() instanceof ISpellModule module ? module : null;
    }
}
