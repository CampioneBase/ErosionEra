package lunastic.erosion_era.basic.mixin.data.server;

import net.minecraft.data.server.AbstractTagProvider;
import net.minecraft.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractTagProvider.class)
public interface AbstractTagProviderInvoker<T> {

    @Invoker("getOrCreateTagBuilder")
    AbstractTagProvider.ObjectBuilder<T> invokerGetOrCreateTagBuilder(TagKey<T> tag);
}
