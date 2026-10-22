package campionebase.erosionera.entity.projectile;

import campionebase.erosionera.magic.spell.*;
import campionebase.erosionera.magic.spell.behaviour.RuneBehaviour;
import campionebase.erosionera.magic.spell.logic.IRuneLogic;
import campionebase.erosionera.registry.ErErEntities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * <h2>魔法弹</h2>
 * 一个可以运动的魔法介质实体。
 *
 * <p>符文的遍历由 {@link IRuneLogic} 负责（本模组内置线性实现），
 * 子弹自身只持有"当前逻辑"与逐 tick 的运行状态（延迟、目标、生命周期、运动）。
 */
public class MagicBullet extends AbstractManaProjectile {

    /** 符文遍历逻辑；游标始终指向**下一个待执行**符文。 */
    private final IRuneLogic logic;
    // 剩余等待时间
    private int remaining;
    // 目标
    @Nullable
    private SpellTarget target;
    // 生命周期
    private int life = 1200; // TODO: 可能会根据魔力值调整

    private final MovementState movement = new MovementState();

    private final Set<UUID> hitEntities = new HashSet<>();

    public MagicBullet(Level level, IRuneLogic logic) {
        super(ErErEntities.MAGIC_BULLET.get(), level);
        this.logic = logic;
    }

    public MagicBullet(EntityType<MagicBullet> type, Level level) {
        super(type, level);
        // 客户端/反序列化占位：空逻辑，不会执行任何符文
        this.logic = RuneSequence.EMPTY.newLogic();
    }

    public MagicBullet(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(ErErEntities.MAGIC_BULLET.get(), level);
    }

    @Override
    public boolean shouldBeSaved() {
        // 魔法子弹实体不会被保存，区块卸载就会被销毁
        return false;
    }

    /** 当前遍历逻辑（存档与调试用）。 */
    public IRuneLogic logic() {
        return this.logic;
    }

    @Override
    public void tick() {
        this.level().getProfiler().push("rune");
        this.runeTick();
        this.level().getProfiler().pop();
        super.tick();
    }

    protected void runeTick() {
        if (!(this.level() instanceof ServerLevel serverLevel)) return;
        // 检查生命周期
        if (--this.life < 0) {
            this.discard();
            return;
        }

        List<SpellEvent> events = this.moveAndCollect(serverLevel);
        // 是否还在延迟中
        if (this.remaining > 0) {
            if (--this.remaining > 0) return;
        }

        // 符文已全部执行
        if (this.logic.isFinished()) {
            this.discard();
            return;
        }

        // 获取上下文
        SpellContext context = new SpellContext(this, serverLevel, this.movement, events);
        context.target = this.target;

        // 当前符文
        RuneEntry entry = this.logic.currentEntry();
        if (entry == null) {
            // 该符文已失效（数据包缺失等）：跳过并继续
            this.logic.advance();
            return;
        }

        // 解析符文
        this.parseBehaviour(entry.behaviour(), serverLevel, context, events, this.getOwner());
        this.logic.advance();
        this.remaining = entry.delay();
    }

    // 移动并射线检测。下一 tick 实体的位置不再和 delta movement 有关，只有 velocity 参与计算
    private List<SpellEvent> moveAndCollect(ServerLevel serverLevel) {
        List<SpellEvent> events = new ArrayList<>();
        this.movement.tick();

        if (this.movement.velocity.lengthSqr() > 1e-6) {
            Vec3 from = this.position();
            Vec3 to = from.add(this.movement.velocity);
            if (!this.movement.passThroughBlocks) {
                BlockHitResult hit = serverLevel.clip(new ClipContext(from, to,
                        ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
                if (hit.getType() == HitResult.Type.BLOCK) {
                    events.add(new SpellEvent.BlockHit(hit.getBlockPos(), hit.getDirection()));
                    to = hit.getLocation().subtract(this.movement.velocity.normalize().scale(0.01));
                    this.movement.velocity = Vec3.ZERO;
                }
            }
            this.setPos(to);
            // 不包含施法者，但不妨碍范围伤害类型的判断
            for (Entity e : serverLevel.getEntities(this, getBoundingBox().inflate(0.5),
                    e -> e.isAlive() && e != this.getOwner() && !this.hitEntities.contains(e.getUUID()))) {
                this.hitEntities.add(e.getUUID());
                events.add(new SpellEvent.EntityHit(e));
            }
        }
        events.add(new SpellEvent.Tick());
        this.setDeltaMovement(Vec3.ZERO);
        return events;
    }

    protected void parseBehaviour(
            RuneBehaviour behaviour,
            ServerLevel level,
            SpellContext context,
            List<SpellEvent> events,
            @Nullable Entity owner
    ) {
        // 构造符文：以"本符文之后**尚未执行**的部分"作为新载体的逻辑
        if (behaviour instanceof RuneBehaviour.Builder builder) {
            IRuneLogic child = this.logic.copy();
            child.advance();
            MagicBullet spawned = builder.create(level, owner, this.position(), this.movement.velocity, child);
            if (spawned != null) {
                level.addFreshEntity(spawned);
            }
        }
        // 修饰符文
        if (behaviour instanceof RuneBehaviour.Decorator decorator) {
            decorator.apply(context);
        }
        // 触发符文
        if (behaviour instanceof RuneBehaviour.Trigger trigger) {
            SpellEvent match = events.stream()
                    .filter(e -> trigger.interestedEvent().contains(e.kind()))
                    .filter(e -> trigger.test(context, e))
                    .findFirst().orElse(null);
            if (match != null) {
                this.target = match.toTarget();
                if (trigger.stopMotionOnTrigger()) {
                    this.movement.velocity = Vec3.ZERO;
                }
            }
        }
        // 行动符文
        if (behaviour instanceof RuneBehaviour.Action action) {
            action.execute(context);
        }
    }

    // 移动状态包装
    public static final class MovementState {
        // 速度向量
        public Vec3 velocity = Vec3.ZERO;
        // 加速度
        public double acceleration = 0;
        // 目标转向
        public Vec3 trackingTarget;
        // 转动插值
        public double trackingTurnRate = 0.2;
        // 是否穿透方块
        public boolean passThroughBlocks = false;

        // 每 tick 修正速度向量
        public void tick() {
            // 转向修正
            if (this.trackingTarget != null && this.velocity.lengthSqr() > 1e-6) {
                Vec3 desired = this.trackingTarget.normalize().scale(this.velocity.length());
                this.velocity = this.velocity.lerp(desired, this.trackingTurnRate);
            }
            // 加速度修正
            if (this.acceleration != 0) {
                double len = this.velocity.length();
                this.velocity = this.velocity.normalize().scale(Math.max(0, len + this.acceleration));
            }
        }
    }
}
