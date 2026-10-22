package campionebase.erosionera.magic;

import campionebase.erosionera.magic.spell.ISpell;

/**
 * 所有魔法（泛用 {@link ISpell} 与非泛用 {@link IMantra}）的公共契约。
 *
 * <p>设计要点（见 docs/magic_rune.md §2）：
 * <ul>
 *   <li><b>法杖只关注本接口</b>——施法类物品不需要知道背后是 Spell 还是 Mantra。</li>
 *   <li>环境密度检查是<b>公共逻辑</b>（所有魔法共有），放在 {@link MagicConditions#densityBetween}。</li>
 *   <li>侵蚀度条件<b>不在本接口内</b>：它由各实现经 {@link IMantra#conditions()} 自行引入，
 *       以保证「魔法体系与侵蚀体系不耦合」（依赖方向为 侵蚀 → 魔法）。</li>
 * </ul>
 */
public interface IMagic {
    /** 资源消耗。 */
    ResourceCost getCost();

    /**
     * 施法前求值一次（<b>不做每 tick 轮询</b>）。
     *
     * @return {@link MagicResult#success()} 表示可施放；否则为失败原因
     */
    MagicResult checkConditions(MagicContext ctx);

    /**
     * 执行施法：消费资源、产生效果。
     *
     * <p>实现应假定 {@link #checkConditions} 已经通过；仍建议对关键前提做防御性判断。
     */
    MagicResult cast(MagicContext ctx);
}
