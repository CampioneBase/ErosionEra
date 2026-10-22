package campionebase.erosionera.magic.spell.behaviour;

import campionebase.erosionera.magic.spell.SpellContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.phys.Vec3;

/**
 * <p>修饰符文 · 速度：把魔法弹的 <b>速率</b> 改为指定档位，方向保持不变。
 *
 * <p>数据包形态：
 * <pre>{@code
 * {
 *   "type": "erosionera:decorator_velocity",
 *   "behaviour": { "velocity": <double> },
 *   "mana": 5,
 *   "delay": 0
 * }
 * }</pre>
 *
 */
public record VelocityDecorator(double velocity) implements RuneBehaviour.Decorator {

    /** 视为"无运动方向"的阈值（长度平方）。 */
    private static final double EPSILON_SQR = 1.0E-6D;

    public static final MapCodec<VelocityDecorator> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.optionalFieldOf("velocity", 0.0D).forGetter(VelocityDecorator::velocity)
    ).apply(instance, VelocityDecorator::new));

    @Override
    public void apply(SpellContext context) {
        Vec3 current = context.movement.velocity;
        // 静止的子弹没有方向可缩放：直接跳过（例如已被 Trigger 停下）
        if (current.lengthSqr() < EPSILON_SQR) {
            return;
        }
        context.movement.velocity = current.normalize().scale(this.velocity);
    }
}
