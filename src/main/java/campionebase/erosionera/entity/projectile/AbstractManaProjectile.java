package campionebase.erosionera.entity.projectile;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

/**
 * <h2> 魔力投掷物抽象类 </h2>
 * 所有包含魔力相关的投掷物的基类
 */
public abstract class AbstractManaProjectile extends Projectile {
    protected AbstractManaProjectile(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    protected AbstractManaProjectile(EntityType<? extends AbstractManaProjectile> type, double x, double y, double z, Level level){
        super(type, level);
        this.setPos(x, y, z);
    }

    protected AbstractManaProjectile(EntityType<? extends AbstractManaProjectile> type, Entity entity, Level level){
        this(type, entity.getX(), entity.getEyeY() - 0.1, entity.getZ(), level);
        this.setOwner(entity);
    }

    @Override
    protected void defineSynchedData() {

    }
}
