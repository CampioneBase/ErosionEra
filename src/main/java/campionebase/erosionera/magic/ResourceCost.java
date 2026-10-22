package campionebase.erosionera.magic;

/**
 * 施法所需资源。
 *
 * <p>按类型化字段设计：后续若要加入侵蚀（EP）以外的消耗（构件耐久、材料、时间），
 * 在这里加字段并同步 {@link #isFree()} 与 {@link #describe()}，不需要改动 {@link IMagic} 的形状。
 *
 * <p>量纲见 docs/mana_density.md §6：1 桶 = 1000 mb = 1000 MP = 1 格魔力池源。
 */
public final class ResourceCost {

    public static final ResourceCost FREE = new ResourceCost(0, 0.0F);

    private final int manaCost;
    private final float erosionCost;

    public ResourceCost(int manaCost, float erosionCost) {
        if (manaCost < 0) {
            throw new IllegalArgumentException("manaCost must be >= 0, was " + manaCost);
        }
        if (erosionCost < 0.0D) {
            throw new IllegalArgumentException("erosionCost must be >= 0, was " + erosionCost);
        }
        this.manaCost = manaCost;
        this.erosionCost = erosionCost;
    }

    public static ResourceCost mana(int amount) {
        return new ResourceCost(amount, 0.0F);
    }

    /**
     * 需要<b>消耗侵蚀度</b>的魔法。
     *
     * <p>注意：这是「消耗/反噬」，与「侵蚀度作为释放条件」是两件事——
     * 后者属于 predicate 条件（docs/magic_mantra.md §2.2），不在这里表达。
     */
    public static ResourceCost erosion(float amount) {
        return new ResourceCost(0, amount);
    }

    /** 魔力消耗（MP）。 */
    public int manaCost() {
        return this.manaCost;
    }

    /** 侵蚀消耗（EP）。 */
    public double erosionCost() {
        return this.erosionCost;
    }

    public boolean needsMana() {
        return this.manaCost > 0;
    }

    public boolean needsErosion() {
        return this.erosionCost > 0.0D;
    }

    public boolean isFree() {
        return this.manaCost == 0 && this.erosionCost == 0.0F;
    }

    public String describe() {
        if (this.isFree()) {
            return "free";
        }
        StringBuilder sb = new StringBuilder();
        if (this.needsMana()) {
            sb.append(this.manaCost).append(" MP");
        }
        if (this.needsErosion()) {
            if (sb.length() > 0) {
                sb.append(" + ");
            }
            sb.append(this.erosionCost).append(" EP");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "ResourceCost{" + this.describe() + "}";
    }
}
