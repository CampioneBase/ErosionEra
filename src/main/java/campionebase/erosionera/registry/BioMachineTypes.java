package campionebase.erosionera.registry;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.api.BioMachineType;
import campionebase.erosionera.api.IBioCamera;
import campionebase.erosionera.api.IBioCore;
import campionebase.erosionera.api.IBioMachine;
import campionebase.erosionera.blockentity.BioNutritionTankBlockEntity;
import campionebase.erosionera.blockentity.BioRedstoneBlockEntity;
import campionebase.erosionera.data.lang.Translation;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.*;

import java.util.function.Supplier;

@Translation(key = BioMachineType.KEY)
public class BioMachineTypes {
    public static final ResourceLocation NAME = ResourceLocation.fromNamespaceAndPath(ErosionEra.MODID, BioMachineType.KEY);
    public static final ResourceKey<Registry<BioMachineType<?>>> KEY = ResourceKey.createRegistryKey(NAME);
    public static final DeferredRegister<BioMachineType<?>> REGISTER = DeferredRegister.create(KEY, ErosionEra.MODID);
    public static final Supplier<IForgeRegistry<BioMachineType<?>>> REGISTRY = REGISTER.makeRegistry(() -> RegistryBuilder
            .<BioMachineType<?>>of(NAME)
    );

    public static <M extends IBioMachine> RegistryObject<BioMachineType<M>> register(String name, BioMachineType.Builder<M> builder){
        return REGISTER.register(name, () -> builder.build(name));
    }

    @Translation.ZH_CN("核心")
    public static final RegistryObject<BioMachineType<IBioCore>> CORE = register("core", BioMachineType
            .builder(IBioCore.class)
    );

    @Translation.ZH_CN("摄像机")
    public static final RegistryObject<BioMachineType<IBioCamera>> CAMERA = register("camera", BioMachineType
            .builder(IBioCamera.class)
    );

    @Translation.ZH_CN("营养液储罐")
    public static final RegistryObject<BioMachineType<BioNutritionTankBlockEntity>> NUTRITION_TANK = register("nutrition_tank", BioMachineType
            .builder(BioNutritionTankBlockEntity.class)
    );

    @Translation.ZH_CN("红石控制")
    public static final RegistryObject<BioMachineType<BioRedstoneBlockEntity>> REDSTONE = register("redstone", BioMachineType
            .builder(BioRedstoneBlockEntity.class)
    );
}
