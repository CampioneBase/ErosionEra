package campionebase.erosionera.magic;

import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.Predicate;

/**
 * 条件与条件类型的注册/管理。
 *
 * <p>两类内容：
 * <ul>
 *   <li><b>条件类型</b>：可复用的 predicate，带 id，
 *       能被数据包按 id 引用（如 {@code erosionera:erosion_tier_at_least}）。</li>
 *   <li><b>条件组合</b>：把若干条件按 AND 组合。</li>
 * </ul>
 *
 * <p>第三方模组（例如侵蚀侧）可在自身初始化时注册条件；本类不做任何侵蚀相关假设。
 */
public final class MagicConditions {

    private MagicConditions() {
    }

    /** 全部条件满足（AND）。空列表视为恒真。 */
    public static MagicCondition allOf(MagicCondition... conditions) {
        return ctx -> {
            for (MagicCondition condition : conditions) {
                if (!condition.test(ctx)) {
                    return false;
                }
            }
            return true;
        };
    }

    /** 全部条件满足（AND），列表形态。 */
    public static MagicCondition allOf(List<MagicCondition> conditions) {
        List<MagicCondition> snapshot = List.copyOf(conditions);
        return ctx -> {
            for (MagicCondition condition : snapshot) {
                if (!condition.test(ctx)) {
                    return false;
                }
            }
            return true;
        };
    }

    /**
     * 环境魔力密度落在 {@code [min, max]} 区间内。
     *
     * <p>密度是<b>所有魔法的必选条件</b>（docs/magic_rune.md §2.2），
     * 但区间边界因术式而异，因此以工厂形式提供而非写死。
     * 密度未知（{@link Double#NaN}）时视为<b>不通过</b>——宁可不放，也不放错。
     */
    public static MagicCondition densityBetween(double min, double max) {
        return ctx -> ctx.hasManaDensity() && ctx.manaDensity() >= min && ctx.manaDensity() <= max;
    }

    /** 环境魔力密度不低于 {@code min}。Mantra 的 D4 启动下界用这个。 */
    public static MagicCondition densityAtLeast(double min) {
        return ctx -> ctx.hasManaDensity() && ctx.manaDensity() >= min;
    }

    /**
     * 环境魔力密度低于 {@code max}。泛用魔法的 D5 失效边界用这个。
     *
     * <p>注意 D5 的"规则不再成立、强行施法对自己爆炸"是<b>危险行为</b>而非单纯拒绝，
     * 因此 {@code max} 之上应当由"允许施法但反噬"的路径处理，而不是简单地返回 false。
     */
    public static MagicCondition densityBelow(double max) {
        return ctx -> ctx.hasManaDensity() && ctx.manaDensity() < max;
    }

    /** 必须有目标。 */
    public static MagicCondition requiresTarget() {
        return ctx -> ctx.target().isPresent();
    }

    /** 自定义条件的匿名实现（无 id，仅用于内部常量）。 */
    public static MagicCondition of(Predicate<MagicContext> predicate) {
        return predicate::test;
    }
}
