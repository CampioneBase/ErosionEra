package campionebase.erosionera.magic.spell.logic;

import campionebase.erosionera.magic.spell.RuneSequence;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * 默认的**线性**符文逻辑：按列表顺序逐一执行。
 *
 * <p>游标属于本实例；数据 ({@link RuneSequence}) 不可变，可安全共享。
 */
public final class LinearRuneLogic implements IRuneLogic {

    private final RuneSequence data;
    private int cursor;

    public LinearRuneLogic(RuneSequence data) {
        this(data, 0);
    }

    public LinearRuneLogic(RuneSequence data, int cursor) {
        this.data = data;
        this.cursor = Math.max(0, cursor);
    }

    @Override
    public @Nullable ResourceLocation currentId() {
        return this.data.get(this.cursor);
    }

    @Override
    public boolean advance() {
        if (this.cursor < this.data.size()) {
            this.cursor++;
        }
        return !this.isFinished();
    }

    @Override
    public boolean isFinished() {
        return this.cursor >= this.data.size();
    }

    @Override
    public int remaining() {
        return Math.max(0, this.data.size() - this.cursor);
    }

    @Override
    public int size() {
        return this.data.size();
    }

    @Override
    public int totalMana() {
        return this.data.totalMana();
    }

    @Override
    public IRuneLogic copy() {
        return new LinearRuneLogic(this.data, this.cursor);
    }

    /** 原始数据（只读用途）。 */
    public RuneSequence data() {
        return this.data;
    }

    /** 当前游标位置（存档与调试用）。 */
    public int cursor() {
        return this.cursor;
    }

    @Override
    public String toString() {
        return "LinearRuneLogic{cursor=" + this.cursor + ", size=" + this.data.size() + '}';
    }
}
