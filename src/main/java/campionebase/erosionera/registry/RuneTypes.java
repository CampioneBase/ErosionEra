package campionebase.erosionera.registry;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.magic.spell.RuneCategory;
import campionebase.erosionera.magic.spell.RuneType;
import campionebase.erosionera.magic.spell.behaviour.EntityHitTrigger;
import campionebase.erosionera.magic.spell.behaviour.ExplodeAction;
import campionebase.erosionera.magic.spell.behaviour.SimpleBuilder;
import campionebase.erosionera.magic.spell.behaviour.VelocityDecorator;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/**
 * 符文类型注册表。
 *
 * <p>注册的是**类型**（功能谓词 + 工厂），具体数值由数据包 {@code data/<ns>/runes/*.json} 提供。
 */
public class RuneTypes {

    public static final ResourceLocation NAME = ResourceLocation.fromNamespaceAndPath(ErosionEra.MODID, RuneType.KEY);
    public static final ResourceKey<Registry<RuneType<?>>> KEY = ResourceKey.createRegistryKey(NAME);
    public static final DeferredRegister<RuneType<?>> REGISTER = DeferredRegister.create(KEY, ErosionEra.MODID);
    public static final Supplier<IForgeRegistry<RuneType<?>>> REGISTRY = REGISTER.makeRegistry(() -> RegistryBuilder
            .<RuneType<?>>of(NAME)
    );

    // ------------------------------------------------------------------ 内置符文类型

    /** 构造 · 简单：直线运动的魔法弹。 */
    public static final RegistryObject<RuneType<SimpleBuilder>> BUILDER_SIMPLE =
            REGISTER.register("builder_simple", () -> new RuneType<>(RuneCategory.BUILDER, SimpleBuilder.CODEC));

    /** 修饰 · 速度：改变速率档位。 */
    public static final RegistryObject<RuneType<VelocityDecorator>> DECORATOR_VELOCITY =
            REGISTER.register("decorator_velocity",
                    () -> new RuneType<>(RuneCategory.DECORATOR, VelocityDecorator.CODEC));

    /** 触发 · 实体：碰到实体触发。 */
    public static final RegistryObject<RuneType<EntityHitTrigger>> TRIGGER_ENTITY_HIT =
            REGISTER.register("trigger_entity_hit",
                    () -> new RuneType<>(RuneCategory.TRIGGER, EntityHitTrigger.codec()));

    /** 行动 · 爆炸：范围伤害。 */
    public static final RegistryObject<RuneType<ExplodeAction>> ACTION_EXPLODE =
            REGISTER.register("action_explode", () -> new RuneType<>(RuneCategory.ACTION, ExplodeAction.CODEC));

    // ------------------------------------------------------------------ 注册表 codec

    /**
     * 懒加载的注册表 codec。
     *
     * <p><b>不能写成 {@code static final Codec<...> CODEC = REGISTRY.get().getCodec();}</b>：
     * 该字段会在<b>类初始化</b>时求值，而 {@code REGISTRY.get()} 在
     * {@code NewRegistryEvent} 触发前为 {@code null}，会直接 NPE。
     * 更隐蔽的是类加载顺序耦合——任何引用本类的静态字段都会触发这条链。
     */
    private static Codec<RuneType<?>> codec;

    public static Codec<RuneType<?>> codec() {
        if (codec == null) {
            codec = REGISTRY.get().getCodec();
        }
        return codec;
    }
}
