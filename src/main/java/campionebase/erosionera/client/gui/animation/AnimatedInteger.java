package campionebase.erosionera.client.gui.animation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class AnimatedInteger extends ScreenAnimation {
    private final float secondsPerUnit;
    @NotNull
    private final Easing easing;

    private float current;
    private int value;
    private int start;
    private int target;
    private float elapsed;
    private float duration;

    public AnimatedInteger(int init) {
        this(init, 1.0F, x -> x);
    }

    public AnimatedInteger(int init, float secondsPerUnit) {
        this(init, secondsPerUnit, x -> x);
    }

    public AnimatedInteger(int init, float secondsPerUnit, @NotNull Easing easing) {
        Objects.requireNonNull(easing);
        this.secondsPerUnit = Math.max(secondsPerUnit, 0.0001F);
        this.easing = easing;
        this.snapTo(init);
    }

    @Override
    protected boolean update(float dt) {
        if (this.value == this.target) {
            return true;
        }

        this.elapsed += Math.max(dt, 0.0F);
        float progress = this.duration <= 0.0F
                ? 1.0F
                : Math.min(this.elapsed / this.duration, 1.0F);

        progress = progress <= 0.0F ? 0.0F : this.easing.apply(progress);
        this.current = this.start + (this.target - this.start) * progress;
        this.value = Math.round(this.current);

        if (progress >= 1.0F) {
            this.value = this.target;
            return true;
        }
        return false;
    }

    public void setTarget(int target) {
        if (this.target == target && !this.finished) {
            return;
        }
        this.start = this.value;
        this.current = this.start;
        this.target = target;
        this.elapsed = 0.0F;
        this.duration = Math.max(Math.abs(target - this.start) * this.secondsPerUnit, 0.0001F);
        this.started = false;
        this.finished = false;
    }

    public void snapTo(int value) {
        this.current = this.value = this.start = this.target = value;
        this.elapsed = 0.0F;
        this.duration = 0.0F;
        this.started = false;
        this.finished = false;
    }

    public int value() {
        return this.value;
    }
}
