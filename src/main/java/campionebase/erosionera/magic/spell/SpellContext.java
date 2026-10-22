package campionebase.erosionera.magic.spell;

import campionebase.erosionera.entity.projectile.MagicBullet;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class SpellContext {
    public final MagicBullet bullet;
    public final ServerLevel level;
    public final MagicBullet.MovementState movement;
    public final List<SpellEvent> events;
    @Nullable
    public SpellTarget target;

    public SpellContext(MagicBullet bullet, ServerLevel level, MagicBullet.MovementState movement, List<SpellEvent> events) {
        this.bullet = bullet;
        this.level = level;
        this.movement = movement;
        this.events = events;
    }
}
