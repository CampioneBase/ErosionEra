package lunastic.erosion_era.mixin.player;

import lunastic.erosion_era.ErosionEraMod;
import lunastic.erosion_era.init.ErErStatusEffects;
import lunastic.erosion_era.world.data.PlayerData;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin{

    // 目标实例对象
    private PlayerEntity _this;

    // 像构造器添加侵蚀度初始值
    @Inject(at = @At("TAIL"), method = "<init>")
    private void init(CallbackInfo info){
        this._this = ((PlayerEntity)(Object)this);
        PlayerData.put(_this, PlayerData.create()); // 与数据池建立链接
    }

    @Inject(at = @At("TAIL"), method = "tick")
    private void erosionTick(CallbackInfo info){
        if(_this.hasStatusEffect(ErErStatusEffects.EROSION_EFFECT)){
            StatusEffectInstance sei = _this.getStatusEffect(ErErStatusEffects.EROSION_EFFECT);
            if(sei == null) return;
            this.getData().tick();
            this.showErosion(sei);
            // 目前还不清楚为啥这个BUFF持续时间结束后不能自动消除
            // 在此处手动添加消除
            if(sei.getDuration() <= 0) _this.removeStatusEffect(ErErStatusEffects.EROSION_EFFECT);
        }
    }

    private void showErosion(StatusEffectInstance sei){
        ErosionEraMod.LOGGER.info("{} erosion: {} with level {} in {} tick(s)",
                _this.getName().getString(),
                this.getData().erosionData.getErosion(),
                this.getData().erosionData.getErosionLevel(),
                sei.getDuration()
        );
    }

    // 从nbt读取数据
    @Inject(method = "readCustomDataFromNbt", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/nbt/NbtCompound;getList(Ljava/lang/String;I)Lnet/minecraft/nbt/NbtList;"
            , shift = At.Shift.AFTER))
    private void readErosionNbt(NbtCompound nbt, CallbackInfo ci){
        this.getData().readNbt(nbt);
        ErosionEraMod.LOGGER.info("Load Data({}): erosion {}, erosionLevel {}",
                _this.getName().getString(),
                this.getData().erosionData.getErosion(),
                this.getData().erosionData.getErosionLevel()
        );
    }

    // 向nbt添加数据
    @Inject(method = "writeCustomDataToNbt", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/nbt/NbtCompound;put(Ljava/lang/String;Lnet/minecraft/nbt/NbtElement;)Lnet/minecraft/nbt/NbtElement;"
            , shift = At.Shift.AFTER))
    private void writeErosionNbt(NbtCompound nbt, CallbackInfo ci){
        this.getData().writeNbt(nbt);
//        nbt.putInt("EE_Erosion", this.getData().erosionData.getErosion());
//        nbt.putInt("EE_ErosionLevel", this.getData().erosionData.getErosionLevel());
        ErosionEraMod.LOGGER.info("Save Data({}): erosion {}, erosionLevel {}",
                _this.getName().getString(),
                this.getData().erosionData.getErosion(),
                this.getData().erosionData.getErosionLevel()
        );
    }
    
    private PlayerData getData(){
        return PlayerData.get(this._this);
    }
}
