package campionebase.erosionera.client.gui.animation;

import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class AnimatedCharacter extends ScreenAnimation {
    public enum Mode {
        ENTER,
        EXIT,
        CHANGE
    }

    public enum Direction {
        HEAD,
        TAIL,
        RANDOM
    }

    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";

    private final float secondsPerCharacter;
    private final Random random = new Random();
    private final List<Integer> randomOrder = new ArrayList<>();

    private String value;
    private String from;
    private String target;
    private Mode mode = Mode.CHANGE;
    private Direction direction = Direction.HEAD;
    private float elapsed;
    private float duration;
    private float alpha = 1.0F;

    public AnimatedCharacter(String initial) {
        this(initial, 0.045F);
    }

    public AnimatedCharacter(String initial, float secondsPerCharacter) {
        this.secondsPerCharacter = Math.max(secondsPerCharacter, 0.0001F);
        this.value = this.from = this.target = initial == null ? "" : initial;
    }

    @Override
    protected boolean update(float dt) {
        if (this.value.equals(this.target) && this.mode == Mode.CHANGE) {
            this.alpha = 1.0F;
            return true;
        }

        this.elapsed += Math.max(dt, 0.0F);
        float progress = this.duration <= 0.0F
                ? 1.0F
                : Math.min(this.elapsed / this.duration, 1.0F);

        this.value = switch (this.mode) {
            case ENTER -> this.renderEnter(progress);
            case EXIT -> this.renderExit(progress);
            case CHANGE -> this.renderChange(progress);
        };

        this.alpha = switch (this.mode) {
            case ENTER -> progress;
            case EXIT -> 1.0F - progress;
            case CHANGE -> 1.0F;
        };

        if (progress >= 1.0F) {
            this.value = this.mode == Mode.EXIT ? "" : this.target;
            this.alpha = this.mode == Mode.EXIT ? 0.0F : 1.0F;
            return true;
        }
        return false;
    }

    public void enter(String target, Direction direction) {
        this.startAnimation(Mode.ENTER, this.value == null ? "" : this.value,
                target == null ? "" : target, direction);
    }

    public void exit(Direction direction) {
        this.startAnimation(Mode.EXIT, this.value == null ? "" : this.value, "", direction);
    }

    public void change(String target, Direction direction) {
        this.startAnimation(Mode.CHANGE, this.value == null ? "" : this.value,
                target == null ? "" : target, direction);
    }

    public String value() {
        return this.value == null ? "" : this.value;
    }

    public String target() {
        return this.target == null ? "" : this.target;
    }

    public float alpha() {
        return this.alpha;
    }

    public Mode mode() {
        return this.mode;
    }

    public Direction direction() {
        return this.direction;
    }

    private void startAnimation(Mode mode, String from, String target, Direction direction) {
        this.mode = mode;
        this.from = from;
        this.target = target;
        this.direction = direction == null ? Direction.HEAD : direction;
        this.elapsed = 0.0F;
        this.duration = Math.max(
                Math.max(from.length(), target.length()) * this.secondsPerCharacter,
                0.0001F);
        this.value = from;
        this.alpha = mode == Mode.ENTER ? 0.0F : 1.0F;
        this.started = false;
        this.finished = false;
        this.prepareRandomOrder(Math.max(from.length(), target.length()));
    }

    private void prepareRandomOrder(int length) {
        this.randomOrder.clear();
        for (int i = 0; i < length; i++) {
            this.randomOrder.add(i);
        }
        Collections.shuffle(this.randomOrder, this.random);
    }

    private String renderEnter(float progress) {
        char[] result = new char[this.target.length()];
        for (int i = 0; i < result.length; i++) {
            float local = this.localProgress(i, result.length, progress);
            if (local <= 0.0F) {
                result[i] = ' ';
            } else if (local >= 1.0F) {
                result[i] = this.target.charAt(i);
            } else {
                result[i] = this.randomCharacter(this.target.charAt(i));
            }
        }
        return new String(result);
    }

    private String renderExit(float progress) {
        char[] result = new char[this.from.length()];
        for (int i = 0; i < result.length; i++) {
            float local = this.localProgress(i, result.length, progress);
            if (local <= 0.0F) {
                result[i] = this.from.charAt(i);
            } else if (local >= 1.0F) {
                result[i] = ' ';
            } else {
                result[i] = this.randomCharacter(this.from.charAt(i));
            }
        }
        return new String(result);
    }

    private String renderChange(float progress) {
        int length = Math.max(this.from.length(), this.target.length());
        char[] result = new char[length];
        for (int i = 0; i < length; i++) {
            char oldChar = i < this.from.length() ? this.from.charAt(i) : ' ';
            char targetChar = i < this.target.length() ? this.target.charAt(i) : ' ';
            float local = this.localProgress(i, length, progress);
            if (local <= 0.0F) {
                result[i] = oldChar;
            } else if (local >= 1.0F) {
                result[i] = targetChar;
            } else {
                result[i] = this.randomCharacter(targetChar == ' ' ? oldChar : targetChar);
            }
        }
        return new String(result).stripTrailing();
    }

    private float localProgress(int index, int length, float progress) {
        if (length <= 0) {
            return 1.0F;
        }

        int order = switch (this.direction) {
            case HEAD -> index;
            case TAIL -> length - 1 - index;
            case RANDOM -> this.randomOrder.isEmpty()
                    ? index
                    : this.randomOrder.get(Math.min(index, this.randomOrder.size() - 1));
        };
        return Mth.clamp(progress * (length + 1) - order, 0.0F, 1.0F);
    }

    private char randomCharacter(char reference) {
        if (Character.isWhitespace(reference)) {
            return ' ';
        }

        String source;
        if (Character.isDigit(reference)) {
            source = DIGITS;
        } else if (Character.isLetter(reference)) {
            source = LETTERS;
        } else {
            source = LETTERS + DIGITS;
        }
        return source.charAt(this.random.nextInt(source.length()));
    }
}
