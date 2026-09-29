package campionebase.erosionera.client.gui.animation;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemFlyAnimation extends TimedAnimation {
    public enum Mode {
        FADE_IN,
        FADE_OUT,
        MOVE
    }

    private final ItemStack itemStack;
    private final float fromX;
    private final float fromY;
    private final float toX;
    private final float toY;
    private final Mode mode;
    private final Easing easing;
    private final float moveDuration = 0.4F;
    private final float fadeDuration = 0.3F;

    private float currentX;
    private float currentY;
    private float currentAlpha;

    public ItemFlyAnimation(ItemStack itemStack, float fromX, float fromY, float toX, float toY) {
        this(itemStack, fromX, fromY, toX, toY, Mode.MOVE, Easing.EASE_OUT_CUBIC);
    }

    public ItemFlyAnimation(ItemStack itemStack,
                            float fromX, float fromY,
                            float toX, float toY,
                            Mode mode) {
        this(itemStack, fromX, fromY, toX, toY, mode, Easing.EASE_OUT_CUBIC);
    }

    public ItemFlyAnimation(ItemStack itemStack,
                            float fromX, float fromY,
                            float toX, float toY,
                            Mode mode, Easing easing) {
        this.itemStack = itemStack;
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
        this.mode = mode;
        this.easing = easing;
        this.currentX = fromX;
        this.currentY = fromY;
        this.currentAlpha = mode == Mode.FADE_IN ? 0.0F : 1.0F;
    }

    @Override
    protected float total() {
        return this.mode == Mode.MOVE ? this.moveDuration : this.fadeDuration;
    }

    @Override
    protected void apply(float elapsed, float normalized) {
        float progress = Mth.clamp(normalized, 0.0F, 1.0F);
        float eased = progress <= 0.0F ? 0.0F : this.easing.apply(progress);

        switch (this.mode) {
            case MOVE -> {
                this.currentX = Mth.lerp(eased, this.fromX, this.toX);
                this.currentY = Mth.lerp(eased, this.fromY, this.toY);
                this.currentAlpha = 1.0F;
            }
            case FADE_IN -> {
                this.currentX = this.fromX;
                this.currentY = this.fromY;
                this.currentAlpha = eased;
            }
            case FADE_OUT -> {
                this.currentX = this.fromX;
                this.currentY = this.fromY;
                this.currentAlpha = 1.0F - eased;
            }
        }
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.currentAlpha);
        graphics.renderItem(this.itemStack, (int) this.currentX, (int) this.currentY);
        graphics.flush();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
