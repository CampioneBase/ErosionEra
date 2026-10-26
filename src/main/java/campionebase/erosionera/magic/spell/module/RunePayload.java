package campionebase.erosionera.magic.spell.module;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.magic.IMagic;
import campionebase.erosionera.magic.spell.RuneSequence;
import campionebase.erosionera.magic.spell.RuneSequenceSpell;
import campionebase.erosionera.magic.spell.RuneUnits;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

/**
 * 符文序列载荷：魔法石里"一个魔法"的其中一种实现。
 *
 * <p>持久化的是**符文 id 列表**（magic_rune.md §3.4），术式实例在运行时构造。
 * 解析出的魔法是 {@link RuneSequenceSpell}。
 */
public final class RunePayload implements SpellPayload {

    public static final ResourceLocation TYPE =
            ResourceLocation.fromNamespaceAndPath(ErosionEra.MODID, "rune");

    private final RuneSequence sequence;

    public RunePayload(RuneSequence sequence) {
        this.sequence = sequence == null ? RuneSequence.EMPTY : sequence;
    }

    public RuneSequence sequence() {
        return this.sequence;
    }

    @Override
    public ResourceLocation type() {
        return TYPE;
    }

    @Override
    public IMagic magic() {
        return new RuneSequenceSpell(this.sequence);
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
