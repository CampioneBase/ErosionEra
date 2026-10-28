package campionebase.erosionera.magic;

import campionebase.erosionera.magic.resource.ResourceCost;

/**
 * 所有魔法的公共契约。
 */
public interface IMagic {
    /** 资源消耗。 */
    ResourceCost getCost();

    /**
     * 施法条件
     */
    MagicResult checkConditions(MagicContext ctx);

    /**
     * 执行施法
     */
    MagicResult cast(MagicContext ctx);
}
