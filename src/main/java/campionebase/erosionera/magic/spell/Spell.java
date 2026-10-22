package campionebase.erosionera.magic.spell;

import campionebase.erosionera.entity.projectile.MagicBullet;
import campionebase.erosionera.magic.MagicContext;
import campionebase.erosionera.magic.MagicResult;
import campionebase.erosionera.magic.ResourceCost;
import campionebase.erosionera.magic.spell.behaviour.RuneBehaviour;
import campionebase.erosionera.magic.spell.logic.IRuneLogic;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

public class Spell implements ISpell {

    private final RuneSequence sequence;

    public Spell(RuneSequence sequence) {
        this.sequence = sequence == null ? RuneSequence.EMPTY : sequence;
    }

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
        if (!result.isSuccess()) return result;
        // 施法资源判断
        if (!ctx.manaPool().hasMana(this.getCost().manaCost())) {
            return MagicResult.resource(this.getCost());
        }

        if (ctx.caster() == null) return MagicResult.error("no caster");
        if (this.sequence.isEmpty()) return MagicResult.error("empty sequence");

        // 起始符文必须是构造符文
        RuneEntry entry = this.sequence.get(0) == null ? null : RuneEntry.get(this.sequence.get(0));
        if (entry == null || !(entry.behaviour() instanceof RuneBehaviour.Builder builder)) {
            return MagicResult.error("sequence must start with a builder rune");
        }
        if (!(ctx.level() instanceof ServerLevel serverLevel)) {
            return MagicResult.error("server only");
        }

        // 每颗子弹一份独立逻辑；游标越过 Builder 自身（它在此刻已被执行）
        IRuneLogic logic = this.sequence.newLogic();
        logic.advance();

        MagicBullet bullet = builder.create(serverLevel, ctx.caster(),
                ctx.caster().getEyePosition(), ctx.caster().getLookAngle(), logic);
        if (bullet == null) {
            return MagicResult.error("builder produced nothing");
        }
        // 资源结算放在成功后，避免失败时白扣
        ctx.manaPool().consumeMana(this.getCost().manaCost());
        serverLevel.addFreshEntity(bullet);
        return result;
    }

    @Override
    public RuneSequence sequence() {
        return this.sequence;
    }

    /** 便捷：序列里第一个（构造）符文的 id。 */
    public ResourceLocation firstRuneId() {
        return this.sequence.get(0);
    }
}
