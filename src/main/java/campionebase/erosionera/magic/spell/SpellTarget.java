package campionebase.erosionera.magic.spell;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;

public interface SpellTarget {
    record EntityTarget(Entity entity) implements SpellTarget {}
    record BlockTarget(BlockPos pos, Direction direction) implements SpellTarget {}
}
