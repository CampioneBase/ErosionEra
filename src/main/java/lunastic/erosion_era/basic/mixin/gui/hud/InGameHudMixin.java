package lunastic.erosion_era.basic.mixin.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import lunastic.erosion_era.basic.data.ErosionData;
import lunastic.erosion_era.basic.data.PlayerExtraData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value= EnvType.CLIENT)
@Mixin(InGameHud.class)
public class InGameHudMixin {

    // 目标实例对象
    private final InGameHud _this = (InGameHud) (Object) this;
    // 目标访问对象
    private final InGameHudAccessor _this_ = (InGameHudAccessor) _this;

    @Inject(method = "renderExperienceBar", at = @At("TAIL"))
    private void renderErosionBar(MatrixStack matrices, int originalX, CallbackInfo ci){
        PlayerEntity player = _this_.getClient().player;
        if(player == null) return;
        ErosionData data = ErosionData.get(player);

        // 使用原版的Sprite textures/gui/bars.png
        RenderSystem.setShaderTexture(0, new Identifier("textures/gui/bars.png"));
        // 绘制坐标X Y 长度
        int x, y, l;
        // 总长度（就是素材图长度）
        int length = 182;
        // 绘制进度条 紫色 (bars.png 50 55）
        y = _this_.getScaledHeight() - 32 - 14;
        // 底框
        _this.drawTexture(matrices, originalX, y, 0, 50, length, 5);
        l = data.isMaxLevel() ? length : data.getErosion() * length / data.getNextLevelErosion();
        // 进度
        _this.drawTexture(matrices, originalX, y, 0, 55, l, 5);
        // _this.drawTexture(matrices, originalX, y, 0, 90, length, 5);
        if(data.getErosion() >= 0){
            String s1 = data.getErosion() + "(" + data.getErosionLevel() + ")"; // todo 目前为测试显示 需要更改
            x = (_this_.getScaledWidth() - _this.getTextRenderer().getWidth(s1)) / 2;
            y = _this_.getScaledHeight() - 32 - 12;
            // 十字描边
            _this.getTextRenderer().draw(matrices, s1, (float)(x + 1), (float)y, 0);
            _this.getTextRenderer().draw(matrices, s1, (float)(x - 1), (float)y, 0);
            _this.getTextRenderer().draw(matrices, s1, (float)x, (float)(y + 1), 0);
            _this.getTextRenderer().draw(matrices, s1, (float)x, (float)(y - 1), 0);
            _this.getTextRenderer().draw(matrices, s1, (float)x, (float)y, ErosionData.COLOR_ARGB);
        }
    }
}
