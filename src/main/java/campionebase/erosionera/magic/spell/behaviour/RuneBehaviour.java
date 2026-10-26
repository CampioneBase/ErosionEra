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

public interface RuneBehaviour {

    interface Builder extends RuneBehaviour {
        /**
         * 生成魔法弹载体。
         *
         * @param spell 该载体**独立**的术式实例（游标已就位，通常已越过 Builder 自身）
         */
        MagicBullet create(
                ServerLevel level,
                @Nullable Entity owner,
                Vec3 position,
                Vec3 velocity,
                ISpell spell
        );
    }

    interface Decorator extends RuneBehaviour {
        void apply(SpellContext context);
    }

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
