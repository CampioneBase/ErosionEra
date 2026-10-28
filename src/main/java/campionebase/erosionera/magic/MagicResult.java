package campionebase.erosionera.magic;

import campionebase.erosionera.magic.resource.ResourceCost;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * 施法结果。
 *
 * <p>失败时携带<b>首个未满足条件</b>的 id（粒度见 docs/magic_mantra.md §6 的 M3），
 * 供 HUD / 提示呈现，且不泄露全部条件。
 */
public final class MagicResult {

    /** 失败原因的内置分类。 */
    public enum Kind {
        /** 成功。 */
        SUCCESS,
        /** 环境魔力密度不在允许区间（所有魔法共有）。 */
        DENSITY,
        /** 资源不足。 */
        RESOURCE,
        /** 缺少目标。 */
        TARGET,
        /** 其他条件未满足（由具体 predicate 提供 id）。 */
        CONDITION,
        /** 实现内部错误或未预期状态。 */
        ERROR
    }

    private static final MagicResult SUCCESS = new MagicResult(Kind.SUCCESS, null, null);

    private final Kind kind;
    @Nullable
    private final ResourceLocation conditionId;
    @Nullable
    private final Component message;

    private MagicResult(Kind kind, @Nullable ResourceLocation conditionId, @Nullable Component message) {
        this.kind = kind;
        this.conditionId = conditionId;
        this.message = message;
    }

    public static MagicResult success() {
        return SUCCESS;
    }

    public static MagicResult density(double density) {
        return new MagicResult(Kind.DENSITY, null,
                Component.translatable("message.erosionera.magic.density",
                        String.format("%.2f", density)));
    }

    public static MagicResult resource(ResourceCost cost) {
        return new MagicResult(Kind.RESOURCE, null,
                Component.translatable("message.erosionera.magic.resource", cost.describe()));
    }

    public static MagicResult target() {
        return new MagicResult(Kind.TARGET, null,
                Component.translatable("message.erosionera.magic.target"));
    }

    public static MagicResult condition(ResourceLocation conditionId) {
        return new MagicResult(Kind.CONDITION, conditionId,
                Component.translatable("message.erosionera.magic.condition",
                        conditionId.toString()));
    }

    public static MagicResult error(String detail) {
        return new MagicResult(Kind.ERROR, null, Component.literal(detail));
    }

    public boolean isSuccess() {
        return this.kind == Kind.SUCCESS;
    }

    public Kind kind() {
        return this.kind;
    }

    /** 未满足条件的 id；仅 {@link Kind#CONDITION} 时有值。 */
    @Nullable
    public ResourceLocation conditionId() {
        return this.conditionId;
    }

    /** 面向玩家的提示。 */
    @Nullable
    public Component message() {
        return this.message;
    }

    @Override
    public String toString() {
        return "MagicResult{" + this.kind
                + (this.conditionId == null ? "" : " " + this.conditionId)
                + '}';
    }
}
