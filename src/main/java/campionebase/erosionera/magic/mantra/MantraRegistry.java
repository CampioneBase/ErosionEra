package campionebase.erosionera.magic.mantra;

import campionebase.erosionera.ErosionEra;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Mantra 注册表：Mantra id → {@link IMantra} 实例。
 *
 * <p>Mantra 目前没有独立的数据包定义格式（docs/magic_mantra.md 的 M1 未定稿），
 * 因此先用**代码注册**：模组/扩展在自己的初始化里{@link #register(ResourceLocation, IMantra)}。
 * 将来若改为数据包驱动，只需替换本类的填充来源，`MantraPayload` 与施法器均不受影响。
 *
 * <p>重复注册抛异常——静默覆盖会让"哪个定义生效"不可追踪。
 */
public final class MantraRegistry {

    private static final Map<ResourceLocation, IMantra> MANTRAS = new LinkedHashMap<>();

    private MantraRegistry() {
        throw new UnsupportedOperationException();
    }

    public static IMantra register(ResourceLocation id, IMantra mantra) {
        IMantra existing = MANTRAS.putIfAbsent(id, mantra);
        if (existing != null) {
            throw new IllegalStateException("Duplicate mantra id: " + id);
        }
        return mantra;
    }

    @Nullable
    public static IMantra get(@Nullable ResourceLocation id) {
        return id == null ? null : MANTRAS.get(id);
    }

    public static boolean contains(ResourceLocation id) {
        return MANTRAS.containsKey(id);
    }

    public static Map<ResourceLocation, IMantra> all() {
        return Collections.unmodifiableMap(MANTRAS);
    }

    /** 仅用于测试与重载。 */
    static void clear() {
        MANTRAS.clear();
        ErosionEra.LOGGER.debug("MantraRegistry cleared");
    }
}
