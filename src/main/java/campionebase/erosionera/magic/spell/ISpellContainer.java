package campionebase.erosionera.magic.spell;

import campionebase.erosionera.magic.IMagic;
import campionebase.erosionera.magic.spell.module.SpellPayload;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * 「可装卸模块」的容器契约（法杖等）。
 *
 * <p>容器只知道自己在装一个 {@link SpellPayload}，<b>不知道</b>模块具体是刻录石还是
 * Mantra 石；配方也只认 {@link campionebase.erosionera.magic.spell.module.ISpellModule}。
 * 新增模块类型不需要改动容器或配方。
 *
 * <p>实现方式：让 <b>Item</b> 实现本接口。默认实现已把整条链路接好，
 * 通常只需要按需覆盖 {@link #isOnCooldown(ItemStack)} 或 {@link #slotCount()}。
 */
public interface ISpellContainer {

    /** 可同时承载的模块数量；默认单槽。多槽需要覆盖负载方法。 */
    default int slotCount() {
        return 1;
    }

    /**
     * 是否处于冷却。
     *
     * <p>按已定裁决：<b>冷却期间禁止装卸</b>，用于堵住"换石重置冷却"的漏洞。
     */
    default boolean isOnCooldown(ItemStack stack) {
        return false;
    }

    // ------------------------------------------------------------------ 载荷

    /** 当前承载的载荷；空槽返回 {@code null}。 */
    @Nullable
    default SpellPayload getPayload(ItemStack stack) {
        return SpellContainerData.get(stack);
    }

    /** 写入载荷。 */
    default void setPayload(ItemStack stack, SpellPayload payload) {
        SpellContainerData.set(stack, payload);
    }

    /** 取出并移除载荷；空槽返回 {@code null}。 */
    @Nullable
    default SpellPayload removePayload(ItemStack stack) {
        return SpellContainerData.remove(stack);
    }

    /** 是否已有承载内容。 */
    default boolean hasPayload(ItemStack stack) {
        return this.getPayload(stack) != null;
    }

    /**
     * 当前承载的魔法（{@link IMagic}）。
     *
     * <p>这是施法器需要知道的**全部**信息：无论容器里装的是符文序列（Spell）、
     * 单个 Mantra，还是扩展模组的其他魔法类型，都收敛到这一个接口。
     * 施法器只需 {@code magic.cast(ctx)}——施法条件与资源消耗由魔法自身负责。
     *
     * @return 空槽或载荷无法解析时返回 {@code null}
     */
    @Nullable
    default IMagic magicOf(ItemStack stack) {
        SpellPayload payload = this.getPayload(stack);
        return payload == null ? null : payload.magic();
    }

    // ------------------------------------------------------------------ 门槛

    /** 是否允许装载：不在冷却中，且还有空槽。 */
    default boolean canBind(ItemStack stack) {
        return !this.isOnCooldown(stack) && !this.hasPayload(stack);
    }

    /** 是否允许卸下：不在冷却中，且确实承载着内容。 */
    default boolean canUnbind(ItemStack stack) {
        return !this.isOnCooldown(stack) && this.hasPayload(stack);
    }

    /** 从物品栈取容器；不是容器物品时返回 {@code null}。 */
    @Nullable
    static ISpellContainer of(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        return stack.getItem() instanceof ISpellContainer container ? container : null;
    }
}
