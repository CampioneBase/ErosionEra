package campionebase.erosionera.client.gui.animation;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemFlyAnimation extends TimedAnimation{
    private final ItemStack itemStack;
    private final float fromX, fromY, toX, toY;
    private final float moveDur = 0.4f, holdDur = 0.3f, fadeDur = 0.3f;
    // 绘制数据
    private float currentX, currentY, currentAlpha;

    public ItemFlyAnimation(ItemStack itemStack, float fromX, float fromY, float toX, float toY){
        this.itemStack = itemStack;
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
    }

    @Override
    protected float total() {
        return this.moveDur + this.holdDur + this.fadeDur;
    }

    @Override
    protected void apply(float elapsed, float normalized) {
        // 移动阶段
        if (elapsed < this.moveDur) {
            float p = Easing.CUBIC.apply(elapsed / this.moveDur);
            this.currentX = Mth.lerp(p, this.fromX, this.toX);
            this.currentY = Mth.lerp(p, this.fromY, this.toY);
            this.currentAlpha = 1.0f;
        }
        // 停顿阶段
        else if (elapsed < this.moveDur + this.holdDur) {
            this.currentX = this.toX;
            this.currentY = this.toY;
            this.currentAlpha = 1.0f;
        }
        // 消失阶段
        else {
            float p = (elapsed - this.moveDur - this.holdDur) / this.fadeDur;
            this.currentX = toX; this.currentY = this.toY;
            this.currentAlpha = Mth.clamp(1.0F - p, 0.0F, 1.0F);
        }
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, this.currentAlpha);
        graphics.renderItem(this.itemStack, (int) this.currentX, (int) this.currentY);
        graphics.flush();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }
}
