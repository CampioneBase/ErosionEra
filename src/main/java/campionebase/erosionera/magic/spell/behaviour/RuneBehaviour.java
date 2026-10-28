package campionebase.erosionera.magic.spell.behaviour;

import campionebase.erosionera.entity.projectile.MagicBullet;
import campionebase.erosionera.magic.spell.ISpell;
import campionebase.erosionera.magic.spell.SpellContext;
import campionebase.erosionera.magic.spell.SpellEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/** 符文行为 */
public interface RuneBehaviour {
    /**
     * <h3>构造符文</h3>主要用于生成 {@link MagicBullet} 实例
     */
    interface Builder extends RuneBehaviour {
        /**
         * 生成魔法弹载体。
         */
        MagicBullet create(
                ServerLevel level,
                @Nullable Entity owner,
                Vec3 position,
                Vec3 velocity,
                ISpell spell
        );
    }

    /** <h3>修饰符文</h3>主要用于修饰/控制 {@link MagicBullet} 实例 */
    interface Decorator extends RuneBehaviour {
        void apply(SpellContext context);
    }

    /** <h3>触发符文</h3>主要用于修饰/控制 {@link MagicBullet} 实例 */
    interface Trigger extends RuneBehaviour {
        @NotNull
        Set<SpellEvent.Kind> interestedEvent();

        boolean test(SpellContext context, SpellEvent event);

        default boolean stopMotionOnTrigger() { return true; }
    }

    interface Action extends RuneBehaviour {
        void execute(SpellContext context);
    }
}
