package campionebase.erosionera.magic;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * 施法上下文：一次施法所需的全部输入。
 *
 * <p>刻意保持精简。字段增删都是<b>破坏性变更</b>（会影响所有 Mantra 的 predicate 实现），
 * 因此只在确实需要时扩展，并同步 docs/magic_mantra.md §6 的 M2。
 */
public final class MagicContext {
    // 施法者，为 null 表示法术无来源
    @Nullable
    private final LivingEntity caster;
    // 法术所处维度
    private final Level level;
    // 法术目标
    @Nullable
    private final LivingEntity target;

    /**
     * 当前环境魔力密度（{@code md_total}）。
     *
     * <p>惰性求值：由调用方（施法器物的使用逻辑）注入，避免在魔法包内直接依赖密度实现。
     * 密度实现见 docs/mana_density.md；在 S2 完成前由调用方传 {@link Double#NaN} 占位。
     */
    private final double manaDensity;
    // 所含魔力资源
    private final IManaPool manaPool;

    public MagicContext(@Nullable LivingEntity caster,
                        Level level,
                        @Nullable LivingEntity target,
                        double manaDensity,
                        IManaPool manaPool) {
        this.caster = caster;
        this.level = level;
        this.target = target;
        this.manaDensity = manaDensity;
        this.manaPool = manaPool;
    }

    @Nullable
    public LivingEntity caster() {
        return this.caster;
    }

    public Level level() {
        return this.level;
    }

    public Optional<LivingEntity> target() {
        return Optional.ofNullable(this.target);
    }

    /** 环境魔力密度；未知时为 {@link Double#NaN}。 */
    public double manaDensity() {
        return this.manaDensity;
    }

    public boolean hasManaDensity() {
        return !Double.isNaN(this.manaDensity);
    }

    public IManaPool manaPool() {
        return this.manaPool;
    }

    /** 施法者是否为客户端侧。客户端不应消费资源。 */
    public boolean isClientSide() {
        return this.level.isClientSide();
    }

    /** 便捷构造：无目标、密度未知、空魔力池。主要用于测试与占位。 */
    public static MagicContext of(LivingEntity caster) {
        return new MagicContext(caster, caster.level(), null, Double.NaN, IManaPool.EMPTY);
    }

    @Override
    public String toString() {
        return "MagicContext{caster=" + (this.caster == null ? "none" : this.caster.getName().getString())
                + ", density=" + (this.hasManaDensity() ? this.manaDensity : "unknown")
                + ", target=" + (this.target == null ? "none" : this.target.getName().getString())
                + '}';
    }
}
