package campionebase.erosionera.magic.mantra;

import campionebase.erosionera.magic.IMagic;
import campionebase.erosionera.magic.MagicCondition;
import campionebase.erosionera.magic.MagicConditions;
import campionebase.erosionera.magic.MagicContext;

import java.util.List;

/**
 * 非泛用魔法 Mantra（原「复杂魔法」，第 4 轮正式定名）。
 *
 * <p>特征（docs/magic_mantra.md §1）：
 * <ul>
 *   <li>释放条件<b>比较严苛</b>——密度是<b>必选</b>（由 {@link MagicConditions#densityBetween} 在 {@link IMagic} 层统一检查）；</li>
 *   <li>其余条件（<b>包括侵蚀度</b>）全部由本接口的 {@link #conditions()} 提供。</li>
 * </ul>
 *
 * <p><b>不耦合的关键</b>：predicate 里是否包含侵蚀判断，交给 Mantra 具体实现决定。
 * 侵蚀侧只提供「可被 predicate 调用的查询」，依赖方向为 侵蚀 → 魔法，反向不成立。
 */
public interface IMantra extends IMagic {

    /**
     * 释放条件列表。
     *
     * <p>求值语义默认为 <b>AND</b>（全部满足才可释放）。
     * 「区间条件」（如侵蚀度需落在 250~400 内，超出反而无法释放）由条件实现自身表达，
     * 接口层不做特殊支持——这与 docs/erosion.md §7.4 的「维持在某区间」同构。
     */
    List<MagicCondition> conditions();

    /** 本 Mantra 是否有额外的条件要求。 */
    default boolean hasConditions() {
        return !this.conditions().isEmpty();
    }

    /** 便捷：判断是否全部条件满足。 */
    default boolean testConditions(MagicContext ctx) {
        for (MagicCondition condition : this.conditions()) {
            if (!condition.test(ctx)) {
                return false;
            }
        }
        return true;
    }
}
