package campionebase.erosionera.magic.spell.behaviour;

import campionebase.erosionera.entity.projectile.MagicBullet;
import campionebase.erosionera.magic.spell.ISpell;
import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * 构造符文 · 简单：生成一个**直线运动、无额外行为数据**的魔法弹实体。
 *
 * <p>这是术式的必要起始符文——没有它就不会产生载体，后续所有符文都无从执行。
 *
 * <p>数据包形态（无 behaviour 字段）：
 * <pre>{@code
 * { "type": "erosionera:builder_simple", "mana": 10, "delay": 0 }
 * }</pre>
 *
 * <p>速度由调用方（施法器）在其后追加 {@code decorator_velocity} 决定；
 * 本符文自身只负责"以给定速度起飞"。
 */
public record SimpleBuilder() implements RuneBehaviour.Builder {

    /** 无字段，用 unit codec。 */
    public static final MapCodec<SimpleBuilder> CODEC = MapCodec.unit(new SimpleBuilder());

    /** 生成点沿速度方向前移的距离，避免紧贴自身碰撞箱导致同 tick 自伤。 */
    private static final double SPAWN_OFFSET = 0.5D;

    /** 视为"无运动方向"的阈值（长度平方）。 */
    private static final double EPSILON_SQR = 1.0E-6D;

    @Override
    public MagicBullet create(
            ServerLevel level,
            @Nullable Entity owner,
            Vec3 position,
            Vec3 velocity,
            ISpell spell
    ) {
        MagicBullet bullet = new MagicBullet(level, spell);
        bullet.setOwner(owner);
        Vec3 spawn = velocity.lengthSqr() > EPSILON_SQR
                ? position.add(velocity.normalize().scale(SPAWN_OFFSET))
                : position;
        bullet.setPos(spawn.x, spawn.y, spawn.z);
        bullet.setDeltaMovement(velocity);
        return bullet;
    }
}
