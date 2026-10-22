package campionebase.erosionera.magic.spell.module;

import campionebase.erosionera.magic.IMagic;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * 可装配进容器（法杖等）的**载荷**。
 *
 * <p>载荷是容器里"装了什么"的抽象，也是整条链的收敛点：
 * <b>任何载荷最终只解析成一个 {@link IMagic}</b>。
 *
 * <ul>
 *   <li>{@link RunePayload} —— 刻录石的符文 id 列表 → 解析为泛用魔法（Spell）</li>
 *   <li>{@link MantraPayload} —— 只含一个 Mantra id → 解析为非泛用魔法（Mantra）</li>
 *   <li>扩展模组可注册自己的载荷类型，同样只需能解析出 {@link IMagic}</li>
 * </ul>
 *
 * <p>因此施法器（法杖）<b>不需要</b>区分 Spell / Mantra / 扩展类型——它只做：
 * {@code payload.magic().cast(ctx)}，施法条件与资源消耗由魔法自身负责。
 *
 * <p>新增载荷类型需到 {@link SpellPayloads} 注册（类型 id → 读取器 + 模块物品工厂），
 * 容器与配方无需改动。
 */
public interface SpellPayload {

    /** 载荷类型 id，用于 NBT 判别与注册表查找。 */
    ResourceLocation type();

    /** 序列化为 NBT（不含类型 id，类型由容器写入）。 */
    CompoundTag save();

    /**
     * 解析成魔法对象。
     *
     * <p>返回 {@code null} 表示当前无法解析（例如引用的 Mantra 未被注册、
     * 数据包缺失等）。调用方必须处理 {@code null}，不要假定非空。
     */
    @Nullable
    IMagic magic();
}
