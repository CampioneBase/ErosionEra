package campionebase.erosionera.magic.resource;

/**
 * <h3>魔力池</h3>
 * 魔法资源的结算抽象。只可以提取魔力值，不可以接受魔力值。
 * @see IManaStorage
 */
public interface IManaPool {

    /** 空池 */
    IManaPool EMPTY = new IManaPool() {
        @Override
        public int mana() {
            return 0;
        }

        @Override
        public int extract(int amount) {
            return 0;
        }
    };

    /** 无限池 */
    IManaPool INF = new IManaPool() {
        @Override
        public int mana() {
            return Integer.MAX_VALUE;
        }

        @Override
        public int extract(int amount) {
            return Math.max(0, amount);
        }
    };

    /** 有限魔力池 */
    static IManaPool of(int init) {
        if (init < 0) throw new IllegalArgumentException("Mana can NOT be negative");
        return new IManaPool() {
            private int mana = init;
            @Override
            public int mana() {
                return Math.max(0, this.mana);
            }

            @Override
            public int extract(int amount) {
                amount = Math.max(0, amount);
                int value = Math.min(amount, this.mana);
                this.mana -= value;
                return value;
            }
        };
    }

    /** 当前可用魔力（MP）。 */
    int mana();

    /**
     * 提取魔力
     * @return 返回实际提取到的魔力值
     */
    int extract(int amount);

    default boolean hasMana(int amount) {
        return this.mana() >= amount;
    }
}
