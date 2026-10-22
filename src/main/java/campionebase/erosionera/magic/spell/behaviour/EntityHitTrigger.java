package campionebase.erosionera.magic.spell.behaviour;

import campionebase.erosionera.magic.spell.SpellContext;
import campionebase.erosionera.magic.spell.SpellEvent;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;

public record EntityHitTrigger(
        Optional<EntityType<?>> entityType,
        boolean stopMotion
) implements RuneBehaviour.Trigger {

    /**
     * 懒加载的 codec。
     *
     * <p>不写成 {@code static final}：{@code ForgeRegistries.ENTITY_TYPES.getCodec()}
     * 依赖注册表已冻结，而静态初始化时机由类加载顺序决定，可能早于注册表冻结。
     */
    private static MapCodec<EntityHitTrigger> _codec;

    public static MapCodec<EntityHitTrigger> codec() {
        if (_codec == null) {
            _codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    ForgeRegistries.ENTITY_TYPES.getCodec().optionalFieldOf("entity").forGetter(EntityHitTrigger::entityType),
                    Codec.BOOL.optionalFieldOf("stop_motion", true).forGetter(EntityHitTrigger::stopMotion)
            ).apply(instance, EntityHitTrigger::new));
        }
        return _codec;
    }

    @Override
    public @NotNull Set<SpellEvent.Kind> interestedEvent() {
        return Set.of(SpellEvent.Kind.ENTITY);
    }

    @Override
    public boolean test(SpellContext context, SpellEvent event) {
        if (!(event instanceof SpellEvent.EntityHit entityHit)) return false;
        // 若没有指定 EntityType 则返回 true
        return this.entityType.map(type -> entityHit.entity().getType() == type).orElse(true);
    }

    @Override
    public boolean stopMotionOnTrigger() { return this.stopMotion; }
}
