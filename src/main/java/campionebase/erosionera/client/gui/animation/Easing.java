package campionebase.erosionera.client.gui.animation;

import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface Easing extends Function<Float, Float> {
    Easing OPPOSITE = x -> 1.0F - x;
    Easing QUAD = x -> x * x;
    Easing EASE_OUT_QUAD = x -> 1.0F - (1.0F - x) * (1.0F - x);
    Easing CUBIC = x -> x * x * x;
    Easing EASE_OUT_CUBIC = x -> 1.0F - (1.0F - x) * (1.0F - x) * (1.0F - x);

    @Override
    default Float apply(Float t) {
        t = Mth.clamp(t, 0.0001f, 1.0f);
        return this._unnormalizedApply(t);
    }

    default Easing compose(@NotNull Easing before){
        Objects.requireNonNull(before);
        return x -> apply(before.apply(x));
    }

    default Easing andThen(@NotNull Easing after){
        Objects.requireNonNull(after);
        return x -> after.apply(apply(x));
    }

    @Deprecated
    float _unnormalizedApply(float input);
}
