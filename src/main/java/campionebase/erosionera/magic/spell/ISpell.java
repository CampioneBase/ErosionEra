package campionebase.erosionera.magic.spell;

import campionebase.erosionera.magic.IMagic;

import javax.annotation.Nullable;

/**
 * 泛用魔法：以 {@link RuneEntry} 为最小单元的<b>组合式</b>魔法。
 *
 * <p>定义性特征：<b>低魔环境下绝对稳定</b>（docs/magic_rune.md §1）。
 * 环境边界：D4 = 安全上界，D5 = 规则不再成立（强行施法对自己爆炸）。
 *
 */
public interface ISpell extends IMagic {

    /** 组成本术式的 Rune 序列 */
    RuneSequence sequence();

    /** 构造本术式的施法器（用于表现与失败提示）；可为空。 */
    @Nullable
    default String getCasterItemId() {
        return null;
    }

    /** 便捷：本术式是否为单一 Rune。 */
    default boolean isSingular() {
        return this.sequence().size() == 1;
    }
}
