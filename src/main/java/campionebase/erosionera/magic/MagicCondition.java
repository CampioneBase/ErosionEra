package campionebase.erosionera.magic;

/**
 * 施法条件。
 *
 * <p>「条件」与「消耗」是两件事：前者决定<b>能不能放</b>，后者决定<b>放了要付什么</b>。
 * 见 {@link ResourceCost}。
 */
@FunctionalInterface
public interface MagicCondition {

    boolean test(MagicContext ctx);

    default String describe() {
        return this.getClass().getSimpleName();
    }
}
