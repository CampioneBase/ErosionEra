package lunastic.erosion_era.util.mixin.entity;

import lunastic.erosion_era.data.ErosionData;
import lunastic.erosion_era.util.data.PlayerExtraData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin{

    // 目标实例对象
    private final PlayerEntity _this = (PlayerEntity) (Object) this;

    // 像构造器添加侵蚀度初始值
    @Inject(at = @At("TAIL"), method = "<init>")
    private void init(CallbackInfo info){
        PlayerExtraData.put(_this, PlayerExtraData.newData()); // 与数据池建立链接
    }

    @Inject(at = @At("TAIL"), method = "tick")
    private void dataTick(CallbackInfo info){
        // 数据tick处理
        PlayerExtraData.get(_this).tick(_this);
    }

    // 从nbt读取数据
    @Inject(method = "readCustomDataFromNbt", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/nbt/NbtCompound;getList(Ljava/lang/String;I)Lnet/minecraft/nbt/NbtList;"
            , shift = At.Shift.AFTER))
    private void readErosionNbt(NbtCompound nbt, CallbackInfo ci){
        PlayerExtraData.get(_this).readNbt(nbt);
    }

    // 向nbt添加数据
    @Inject(method = "writeCustomDataToNbt", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/nbt/NbtCompound;put(Ljava/lang/String;Lnet/minecraft/nbt/NbtElement;)Lnet/minecraft/nbt/NbtElement;"
            , shift = At.Shift.AFTER))
    private void writeErosionNbt(NbtCompound nbt, CallbackInfo ci){
        PlayerExtraData.get(_this).writeNbt(nbt);
    }
}
