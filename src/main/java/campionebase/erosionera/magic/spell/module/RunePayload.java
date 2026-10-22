package campionebase.erosionera.magic.spell.module;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.magic.IMagic;
import campionebase.erosionera.magic.spell.RuneSequence;
import campionebase.erosionera.magic.spell.RuneUnits;
import campionebase.erosionera.magic.spell.Spell;
import campionebase.erosionera.magic.spell.logic.IRuneLogic;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

/**
 * 符文序列载荷：刻录石装进容器的东西。
 *
 * <p>持久化的是**符文 id 列表**（magic_rune.md §3.4），遍历逻辑在运行时构造。
 * 解析出的魔法是泛用魔法 {@link Spell}。
 */
public final class RunePayload implements SpellPayload {

    public static final ResourceLocation TYPE =
            ResourceLocation.fromNamespaceAndPath(ErosionEra.MODID, "runes");

    private final RuneSequence sequence;

    public RunePayload(RuneSequence sequence) {
        this.sequence = sequence == null ? RuneSequence.EMPTY : sequence;
    }

    public RuneSequence sequence() {
        return this.sequence;
    }

    /** 基于本载荷创建**新的**运行时遍历逻辑（每颗子弹一份）。 */
    public IRuneLogic newLogic() {
        return this.sequence.newLogic();
    }

    @Override
    public ResourceLocation type() {
        return TYPE;
    }

    @Override
    public IMagic magic() {
        return new Spell(this.sequence);
    }

    @Override
    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        RuneUnits.write(tag, this.sequence);
        return tag;
    }

    public static RunePayload load(CompoundTag tag) {
        return new RunePayload(RuneUnits.read(tag));
    }

    @Override
    public String toString() {
        return "RunePayload" + this.sequence;
    }
}
