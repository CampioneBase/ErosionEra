package campionebase.erosionera.magic.spell;

import campionebase.erosionera.entity.projectile.MagicBullet;
import campionebase.erosionera.magic.MagicContext;
import campionebase.erosionera.magic.MagicResult;
import campionebase.erosionera.magic.ResourceCost;
import campionebase.erosionera.magic.spell.behaviour.RuneBehaviour;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

/**
 * 由**符文序列**组成的泛用魔法（原 {@code Spell}）。
 *
 * <p>同时是遍历指针：{@link #cursor} 指向**下一个待执行**符文。
 * 每个载体一份实例（见 {@link ISpell} 的契约说明）。
 */
public class RuneSequenceSpell implements ISpell {

    private final RuneSequence sequence;
    private int cursor;

    public RuneSequenceSpell(RuneSequence sequence) {
        this(sequence, 0);
    }

    public RuneSequenceSpell(RuneSequence sequence, int cursor) {
        this.sequence = sequence == null ? RuneSequence.EMPTY : sequence;
        this.cursor = Math.max(0, cursor);
    }

    // ------------------------------------------------------------------ IMagic

    @Override
    public ResourceCost getCost() {
        return ResourceCost.mana(this.sequence.totalMana());
    }

    @Override
    public MagicResult checkConditions(MagicContext ctx) {
        return MagicResult.success();
    }

    @Override
    public MagicResult cast(MagicContext ctx) {
        // 施法条件判断
        MagicResult result = this.checkConditions(ctx);
        if (!result.isSuccess()) {
            return result;
        }
        // 资源判断
        if (!ctx.manaPool().hasMana(this.getCost().manaCost())) {
            return MagicResult.resource(this.getCost());
        }

        if (ctx.caster() == null) {
            return MagicResult.error("no caster");
        }
        if (this.sequence.isEmpty()) {
            return MagicResult.error("empty sequence");
        }
        // 起始符文必须是构造符文
        ResourceLocation firstId = this.sequence.get(0);
        RuneEntry entry = firstId == null ? null : RuneEntry.get(firstId);
        if (entry == null || !(entry.behaviour() instanceof RuneBehaviour.Builder builder)) {
            return MagicResult.error("sequence must start with a builder rune");
        }
        if (!(ctx.level() instanceof ServerLevel serverLevel)) {
            return MagicResult.error("server only");
        }

        // 载体使用**独立**的遍历状态，且游标越过 Builder 自身（它在此刻被执行）
        ISpell payload = this.copy();
        payload.advance();

        MagicBullet bullet = builder.create(serverLevel, ctx.caster(),
                ctx.caster().getEyePosition(), ctx.caster().getLookAngle(), payload);
        if (bullet == null) {
            return MagicResult.error("builder produced nothing");
        }
        // 资源结算放在成功之后，避免失败时白扣
        ctx.manaPool().consumeMana(this.getCost().manaCost());
        serverLevel.addFreshEntity(bullet);
        return result;
    }

    // ------------------------------------------------------------------ 遍历

    @Override
    public @Nullable ResourceLocation currentId() {
        return this.sequence.get(this.cursor);
    }

    @Override
    public boolean advance() {
        if (this.cursor < this.sequence.size()) {
            this.cursor++;
        }
        return !this.isFinished();
    }

    @Override
    public boolean isFinished() {
        return this.cursor >= this.sequence.size();
    }

    @Override
    public int remaining() {
        return Math.max(0, this.sequence.size() - this.cursor);
    }

    @Override
    public int size() {
        return this.sequence.size();
    }

    @Override
    public int totalMana() {
        return this.sequence.totalMana();
    }

    @Override
    public ISpell copy() {
        return new RuneSequenceSpell(this.sequence, this.cursor);
    }

    // ------------------------------------------------------------------ 调试

    /** 当前游标位置（调试与存档用）。 */
    public int cursor() {
        return this.cursor;
    }

    @Override
    public String toString() {
        return "RuneSequenceSpell{cursor=" + this.cursor + ", size=" + this.sequence.size() + '}';
    }
}
