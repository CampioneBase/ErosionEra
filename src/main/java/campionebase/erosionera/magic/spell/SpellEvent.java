package campionebase.erosionera.magic.spell;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SpellEvent {
    enum Kind {TICK, BLOCK, ENTITY, MISC}
    Kind kind();
    @Nullable
    SpellTarget toTarget();
    record Tick() implements SpellEvent{
        @Override
        public Kind kind() {
            return Kind.TICK;
        }

        @Override
        public @Nullable SpellTarget toTarget() {
            return null;
        }
    }

    record BlockHit(BlockPos pos, Direction direction) implements SpellEvent {
        @Override
        public Kind kind() {
            return Kind.BLOCK;
        }

        @Override
        public @NotNull SpellTarget toTarget() {
            return new SpellTarget.BlockTarget(this.pos, this.direction);
        }
    }

    record EntityHit(Entity entity) implements SpellEvent {
        @Override
        public Kind kind() {
            return Kind.ENTITY;
        }

        @Override
        public @NotNull SpellTarget toTarget() {
            return new SpellTarget.EntityTarget(this.entity);
        }
    }
}
