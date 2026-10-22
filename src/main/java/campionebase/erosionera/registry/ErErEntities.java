package campionebase.erosionera.registry;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.datagen.lang.Translation;
import campionebase.erosionera.entity.projectile.MagicBullet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Translation(prefix = "entity")
public class ErErEntities {
    public static final DeferredRegister<EntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ErosionEra.MODID);

    @Translation.Key("魔法弹")
    public static final RegistryObject<EntityType<MagicBullet>> MAGIC_BULLET = register("magic_bullet",
            EntityType.Builder.<MagicBullet>of(MagicBullet::new, MobCategory.MISC)
                    .setCustomClientFactory(MagicBullet::new)
                    .setShouldReceiveVelocityUpdates(true)
                    .fireImmune()
                    .noSummon()
                    .sized(0.5f, 0.5f)
    );

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, EntityType.Builder<T> builder) {
        return REGISTER.register(name, () -> builder.build(name));
    }
}
