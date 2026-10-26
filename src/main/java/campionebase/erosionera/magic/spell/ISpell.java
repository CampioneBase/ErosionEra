package campionebase.erosionera.magic.spell;

import campionebase.erosionera.magic.IMagic;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * 泛用魔法：**由符文序列组成的术式**。
 *
 * <p>本接口同时承载两件事：
 * <ol>
 *   <li>作为 {@link IMagic} 的实现——可被施法器施放（条件检查 + 资源结算 + 产生效果）；</li>
 *   <li>作为**符文遍历指针**——载体（魔法弹）用它按顺序取符文。</li>
 * </ol>
 *
 * <p>原先拆出的 `IRuneLogic` 已取消：它存在的唯一理由是让遍历逻辑与
 * {@link RuneSequence} 解耦，而这个职责由本接口直接承担即可。代价是
 * **实现类变成有状态的**（游标），因此：
 *
 * <ul>
 *   <li>每个载体必须持有**自己的**实例（用 {@link #copy()}）；</li>
 *   <li>不可在多次施法之间复用同一个实例；</li>
 *   <li>数据（{@link RuneSequence}）仍不可变，可安全共享。</li>
 * </ul>
 *
 * <p>扩展模组若要非线性的符文结构，直接实现本接口并自定义 {@link #advance()} 即可。
 */
public interface ISpell extends IMagic {

    // ------------------------------------------------------------------ 遍历

    /** 当前待执行符文的 id；已结束或空序列时返回 {@code null}。 */
    @Nullable
    ResourceLocation currentId();

    /** 当前待执行符文的定义；未注册或已结束时返回 {@code null}。 */
    @Nullable
    default RuneEntry currentEntry() {
        ResourceLocation id = this.currentId();
        return id == null ? null : RuneEntry.get(id);
    }

    /**
     * 指针前进一个位置。
     *
     * @return 前进后是否仍有可执行符文
     */
    boolean advance();

    /** 是否已无可执行符文。 */
    boolean isFinished();

    /** 剩余可执行符文数量。 */
    int remaining();

    /** 符文总数（与游标无关）。 */
    int size();

    /** 整条序列的法力总和（与游标无关）。 */
    int totalMana();

    /**
     * 复制一份**独立**的遍历状态（游标一并复制）。
     *
     * <p>用于：施法时为载体创建新实例、分裂时拷贝"尚未被消耗的符文"。
     */
    ISpell copy();

    // ------------------------------------------------------------------ 便捷

    /** 是否只有一个符文（与游标无关）。 */
    default boolean isSingular() {
        return this.size() == 1;
    }

    /** 构造本术式的施法器 id（用于表现与失败提示）；可为空。 */
    @Nullable
    default String getCasterItemId() {
        return null;
    }
}
