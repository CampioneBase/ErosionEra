package campionebase.erosionera.magic.module;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.magic.IMagic;
import campionebase.erosionera.magic.mantra.MantraRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * Mantra 载荷：数据**只有一个 Mantra id**。
 *
 * <p>与 {@link SpellPayload} 的差别只是数据形态；对容器与施法器而言两者等价——
 * 都通过 {@link #magic()} 收敛成 {@link IMagic}。
 */
public final class MantraPayload implements MagicPayload {

    public static final ResourceLocation TYPE =
            ResourceLocation.fromNamespaceAndPath(ErosionEra.MODID, "mantra");

    private static final String TAG_MANTRA = "mantra";

    private final ResourceLocation mantraId;

    public MantraPayload(ResourceLocation mantraId) {
        this.mantraId = mantraId;
    }

    public ResourceLocation mantraId() {
        return this.mantraId;
    }

    @Override
    public ResourceLocation type() {
        return TYPE;
    }

    @Override
    public @Nullable IMagic magic() {
        // 未注册时返回 null，由调用方降级处理（不抛异常、不崩溃）
        return MantraRegistry.get(this.mantraId);
    }

    @Override
    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString(TAG_MANTRA, this.mantraId.toString());
        return tag;
    }

    @Nullable
    public static MantraPayload load(CompoundTag tag) {
        ResourceLocation id = ResourceLocation.tryParse(tag.getString(TAG_MANTRA));
        return id == null ? null : new MantraPayload(id);
    }

    @Override
    public String toString() {
        return "MantraPayload{" + this.mantraId + '}';
    }
}
