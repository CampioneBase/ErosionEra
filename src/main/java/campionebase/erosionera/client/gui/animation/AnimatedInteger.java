package campionebase.erosionera.client.gui.animation;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class AnimatedInteger extends ScreenAnimation {
    private int value, start, target;
    // 完成一次动画所需秒数
    private final float totalSeconds;
    @NotNull
    private final Easing easing;

    private float current;
    private float elapsed;

    public AnimatedInteger(int init, float totalSeconds) {
        this(init, totalSeconds, x -> x);
    }

    public AnimatedInteger(int init, float totalSeconds, @NotNull Easing easing) {
        Objects.requireNonNull(easing);
        this.totalSeconds = Math.max(totalSeconds, 0.0001F);
        this.easing = easing;
        this.snapTo(init);
    }

    @Override
    protected boolean update(float dt) {
        if (this.value == this.target) {
            return true;
        }

        this.elapsed += Math.max(dt, 0.0F);
        float progress = this.totalSeconds <= 0.0F
                ? 1.0F
                : Math.min(this.elapsed / this.totalSeconds, 1.0F);

        float eased = progress <= 0.0F ? 0.0F : this.easing.apply(progress);
        this.current = this.start + (this.target - this.start) * eased;
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
        this.started = false;
        this.finished = false;
    }

    public void snapTo(int value) {
        this.current = this.value = this.start = this.target = value;
        this.elapsed = this.totalSeconds;
        this.started = false;
        this.finished = false;
    }

    public int value() {
        return this.value;
    }
}
