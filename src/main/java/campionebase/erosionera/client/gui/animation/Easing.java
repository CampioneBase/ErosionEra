package campionebase.erosionera.client.gui.animation;

import net.minecraft.util.Mth;

import java.util.function.Function;

@FunctionalInterface
public interface Easing extends Function<Float, Float> {
    Easing QUAD = x -> x * x;
    Easing EASE_OUT_QUAD = x -> 1.0F - (1.0F - x) * (1.0F - x);
    Easing CUBIC = x -> x * x * x;
    Easing EASE_OUT_CUBIC = x -> 1.0F - (1.0F - x) * (1.0F - x) * (1.0F - x);

    @Override
    default Float apply(Float t) {
        t = Mth.clamp(t, 0.0001f, 1.0f);
        return this._unnormalizedApply(t);
    }

    @Deprecated
    float _unnormalizedApply(float input);
}
