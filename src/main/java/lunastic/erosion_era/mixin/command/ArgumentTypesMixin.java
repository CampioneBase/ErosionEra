package lunastic.erosion_era.mixin.command;

import lunastic.erosion_era.command.argument.ErosionChangeArgumentType;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArgumentTypes.class)
public abstract class ArgumentTypesMixin {

    @Inject(method =
            "register(Lnet/minecraft/util/registry/Registry;)Lnet/minecraft/command/argument/serialize/ArgumentSerializer;",
            at = @At("HEAD")
    )
    private static void register(Registry<ArgumentSerializer<?, ?>> registry, CallbackInfoReturnable<ArgumentSerializer<?, ?>> cir){
        ArgumentTypesInvoker.register(registry, "level_change_type", ErosionChangeArgumentType.class, ConstantArgumentSerializer.of(ErosionChangeArgumentType::create));
    }


}
