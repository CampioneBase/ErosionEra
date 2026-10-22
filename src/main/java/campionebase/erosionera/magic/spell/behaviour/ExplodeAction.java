package campionebase.erosionera.magic.spell.behaviour;

import campionebase.erosionera.entity.projectile.MagicBullet;
import campionebase.erosionera.magic.spell.SpellContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/**
 * 行动符文 · 爆炸：以魔法弹当前位置为中心造成范围伤害。
 *
 * <p>数据包形态：
 * <pre>{@code
 * {
 *   "type": "erosionera:action_explode",
 *   "behaviour": { "radius": 3.0, "damage": 8.0 },
 *   "mana": 20,
 *   "delay": 0
 * }
 * }</pre>
 *
 * <p><b>为什么不用 {@code Level#explode}</b>：原生爆炸的伤害由半径推导，无法直接指定
 * {@code damage}；若先用原生爆炸再补伤害会重复计算。因此这里走**手动范围伤害**
 * （按距离线性衰减）+ 纯表现（粒子与音效），并**不破坏方块**。
 * 若将来需要"会炸方块"的爆炸，再提供独立的 {@code destructive} 开关。
 *
 * <p><b>自伤</b>：按已定裁决，爆炸**会波及施法者自身**（伤害来源记在施法者名下）。
 */
public record ExplodeAction(float radius, float damage) implements RuneBehaviour.Action {

    /** 半径上限，防止数据包写出破坏服务器性能的数值。 */
    public static final float MAX_RADIUS = 32.0F;
    /** 伤害上限。 */
    public static final float MAX_DAMAGE = 1000.0F;

    public static final MapCodec<ExplodeAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.floatRange(0.0F, MAX_RADIUS).optionalFieldOf("radius", 3.0F).forGetter(ExplodeAction::radius),
            Codec.floatRange(0.0F, MAX_DAMAGE).optionalFieldOf("damage", 6.0F).forGetter(ExplodeAction::damage)
    ).apply(instance, ExplodeAction::new));

    @Override
    public void execute(SpellContext context) {
        ServerLevel level = context.level;
        MagicBullet bullet = context.bullet;

        if (this.radius <= 0.0F || this.damage <= 0.0F) {
            return;
        }

        Vec3 center = bullet.position();
        Entity owner = bullet.getOwner();
        // 伤害来源：间接魔法，归属施法者（自杀式组合同样记在施法者名下）
        DamageSource source = level.damageSources().indirectMagic(bullet, owner);

        for (Entity entity : level.getEntities(bullet, bullet.getBoundingBox().inflate(this.radius))) {
            if (!(entity instanceof LivingEntity living)) {
                continue;
            }
            double distance = living.position().distanceTo(center);
            if (distance > this.radius) {
                continue;
            }
            // 线性衰减：中心满伤，边缘 0
            float falloff = (float) (1.0D - distance / this.radius);
            float applied = this.damage * falloff;
            if (applied <= 0.0F) {
                continue;
            }
            living.invulnerableTime = 0;   // 保证同一 tick 内的多次爆炸都能结算
            living.hurt(source, applied);
        }

        level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, center.x, center.y, center.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        level.playSound(null, BlockPos.containing(center), SoundEvents.GENERIC_EXPLODE,
                SoundSource.BLOCKS, 1.0F, 1.0F);
    }
}
