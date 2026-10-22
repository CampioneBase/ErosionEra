package campionebase.erosionera.magic;

/**
 * 魔力池：魔法资源的结算抽象。
 *
 * <p>刻意<b>不</b>引用 Forge 的 capability 类型，让魔法包保持零 Forge 依赖：
 * 谁提供魔力（法杖、魔力机械、临时 buff）由实现方自行接上。
 * 侵蚀侧同理，属于 EP 的池子实现。
 */
public interface IManaPool {

    /** 空池：既不提供也不接受任何魔力。 */
    IManaPool EMPTY = new IManaPool() {
        @Override
        public int getMana() {
            return 0;
        }

        @Override
        public boolean consumeMana(int amount) {
            return amount <= 0;
        }
    };

    /** 当前可用魔力（MP）。 */
    int getMana();

    /**
     * 尝试消费。
     *
     * @return 是否消费成功；不足时应返回 {@code false} 且<b>不做部分消费</b>
     */
    boolean consumeMana(int amount);

    default boolean hasMana(int amount) {
        return this.getMana() >= amount;
    }
}
