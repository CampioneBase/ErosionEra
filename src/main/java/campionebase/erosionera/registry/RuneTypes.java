package campionebase.erosionera.registry;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.magic.spell.RuneType;
import campionebase.erosionera.magic.spell.behaviour.*;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/** 符文类型静态注册表 */
public class RuneTypes {
    public static final ResourceLocation NAME = ResourceLocation.fromNamespaceAndPath(ErosionEra.MODID, RuneType.KEY);
    public static final ResourceKey<Registry<RuneType<?>>> KEY = ResourceKey.createRegistryKey(NAME);
    public static final DeferredRegister<RuneType<?>> TYPES = DeferredRegister.create(KEY, ErosionEra.MODID);
    public static final Supplier<IForgeRegistry<RuneType<?>>> REGISTRY = TYPES.makeRegistry(() -> RegistryBuilder
            .<RuneType<?>>of(NAME)
    );

    // ------------------------------------------------------------------ 内置符文类型

    /** 构造 · 简单：直线运动的魔法弹。 */
    public static final RegistryObject<RuneType<SimpleBuilder>> BUILDER_SIMPLE =
            TYPES.register("builder_simple", () -> new RuneType<>(SimpleBuilder.CODEC));

    /** 修饰 · 速度：改变速率档位。 */
    public static final RegistryObject<RuneType<VelocityDecorator>> DECORATOR_VELOCITY =
            TYPES.register("decorator_velocity", () -> new RuneType<>(VelocityDecorator.CODEC));

    /** 触发 · 实体：碰到实体触发。 */
    public static final RegistryObject<RuneType<EntityHitTrigger>> TRIGGER_ENTITY_HIT =
            TYPES.register("trigger_entity_hit", () -> new RuneType<>(EntityHitTrigger.codec()));

    /** 行动 · 爆炸：范围伤害。 */
    public static final RegistryObject<RuneType<ExplodeAction>> ACTION_EXPLODE =
            TYPES.register("action_explode", () -> new RuneType<>(ExplodeAction.CODEC));

    // ------------------------------------------------------------------ 注册表 codec
    private static Codec<RuneType<?>> _codec;
    public static Codec<RuneType<?>> codec() {
        if (_codec == null) {
            _codec = REGISTRY.get().getCodec();
        }
        return _codec;
    }
}
