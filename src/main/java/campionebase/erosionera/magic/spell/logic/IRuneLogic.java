package campionebase.erosionera.magic.spell.logic;

import campionebase.erosionera.magic.spell.RuneEntry;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * 符文遍历逻辑：**指针控制 + 当前符文**。
 *
 * <p>本模组只提供线性实现 {@link LinearRuneLogic}；扩展模组可实现本接口做成
 * 非线性结构（分支、循环、子程序等"魔法编程"），载体（魔法弹）只依赖本接口。
 *
 * <p><b>契约</b>：
 * <ul>
 *   <li>每个载体持有**自己的**实例；禁止在多颗子弹间共享（游标是可变状态）。</li>
 *   <li>{@link #copy()} 与 {@link #currentId()} 等只读查询必须不改变自身状态。</li>
 *   <li>数据（序列）不可变，游标属于运行时，两者分离。</li>
 * </ul>
 */
public interface IRuneLogic {

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

    /** 逻辑的符文总数（与游标无关）。 */
    int size();

    /** 整条序列的法力总和（与游标无关）。 */
    int totalMana();

    /** 复制一份**独立**的运行时逻辑（游标位置一并复制）。用于分裂与存档恢复。 */
    IRuneLogic copy();
}
