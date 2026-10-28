package campionebase.erosionera.magic.module;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.magic.spell.RuneUnits;
import campionebase.erosionera.registry.ErErItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 载荷类型注册表：把「载荷类型 id」映射到「读取器 + 模块物品工厂」。
 *
 * <p>内置两种：
 * <ul>
 *   <li>{@code erosionera:runes} —— 符文 id 列表 → 刻录石</li>
 *   <li>{@code erosionera:mantra} —— 单个 Mantra id → Mantra 石（物品尚未落地，见下）</li>
 * </ul>
 *
 * <p>扩展模组注册自己的载荷后，容器能持久化它、卸下时能还原成对应模块物品，
 * 配方与容器一概不需要改动。
 */
public final class MagicPayloads {

    /**
     * 载荷类型定义。
     *
     * @param <S> 具体载荷类型
     */
    public interface Type<S extends MagicPayload> {
        /** 从 NBT 读取载荷；数据非法时返回 {@code null}。 */
        @Nullable
        S load(CompoundTag tag);

        /**
         * 把载荷还原成可再次装配的模块物品。
         *
         * <p>若对应模块物品尚不存在，返回 {@link ItemStack#EMPTY}——
         * 卸下配方会据此拒绝匹配，**避免把模块吞掉**。
         */
        ItemStack toModule(S payload);
    }

    private static final Map<ResourceLocation, Type<?>> TYPES = new LinkedHashMap<>();

    static {
        // 内置：符文序列 → 魔法石
        register(SpellPayload.TYPE, new Type<SpellPayload>() {
            @Override
            public SpellPayload load(CompoundTag tag) {
                return SpellPayload.load(tag);
            }

            @Override
            public ItemStack toModule(SpellPayload payload) {
                ItemStack stone = new ItemStack(ErErItems.MAGIC_STONE.get());
                RuneUnits.write(stone.getOrCreateTag(), payload.sequence());
                return stone;
            }
        });

        // 内置：单个 Mantra → Mantra 石（物品未落地前 toModule 返回空，卸下会被拒绝）
        register(MantraPayload.TYPE, new Type<MantraPayload>() {
            @Override
            public MantraPayload load(CompoundTag tag) {
                return MantraPayload.load(tag);
            }

            @Override
            public ItemStack toModule(MantraPayload payload) {
                // TODO: Mantra 石物品落地后替换为：new ItemStack(ErErItems.MANTRA_STONE.get()) + 写入 id
                return ItemStack.EMPTY;
            }
        });
    }

    private MagicPayloads() {
        throw new UnsupportedOperationException();
    }

    /** 注册载荷类型；重复注册抛异常（静默覆盖会让"哪个定义生效"不可追踪）。 */
    public static <S extends MagicPayload> void register(ResourceLocation id, Type<S> type) {
        if (TYPES.containsKey(id)) {
            throw new IllegalStateException("Duplicate spell payload type: " + id);
        }
        TYPES.put(id, type);
    }

    public static boolean contains(ResourceLocation id) {
        return TYPES.containsKey(id);
    }

    /** 按类型 id + NBT 还原载荷；类型未注册或数据非法时返回 {@code null}。 */
    @Nullable
    @SuppressWarnings("unchecked")
    public static MagicPayload load(ResourceLocation id, CompoundTag tag) {
        Type<?> type = TYPES.get(id);
        if (type == null) {
            ErosionEra.LOGGER.warn("Unknown spell payload type {}, payload ignored", id);
            return null;
        }
        try {
            return ((Type<MagicPayload>) type).load(tag);
        } catch (Exception e) {
            ErosionEra.LOGGER.warn("Failed to read spell payload of type {}: {}", id, e.toString());
            return null;
        }
    }

    /**
     * 把载荷还原成模块物品（卸下时用）。
     *
     * @return 未知类型或工厂不可用时返回 {@link ItemStack#EMPTY}
     */
    @SuppressWarnings("unchecked")
    public static ItemStack toModule(MagicPayload payload) {
        Type<?> type = TYPES.get(payload.type());
        if (type == null) {
            ErosionEra.LOGGER.warn("No module factory for spell payload type {}", payload.type());
            return ItemStack.EMPTY;
        }
        return ((Type<MagicPayload>) type).toModule(payload);
    }

    /** 该载荷当前能否被还原成模块物品（卸下配方据此决定是否匹配）。 */
    public static boolean canProduceModule(MagicPayload payload) {
        return !toModule(payload).isEmpty();
    }
}
