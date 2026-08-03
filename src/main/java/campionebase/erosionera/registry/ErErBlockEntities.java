package campionebase.erosionera.registry;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.blockentity.BioCameraBlockEntity;
import campionebase.erosionera.blockentity.BioConnectorBlockEntity;
import campionebase.erosionera.blockentity.BioControllerBlockEntity;
import campionebase.erosionera.blockentity.BioNutritionTankBlockEntity;
import com.mojang.datafixers.types.Type;
import net.minecraft.Util;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ErErBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ErosionEra.MODID);

    public static final RegistryObject<BlockEntityType<BioNutritionTankBlockEntity>> BIO_NUTRITION_TANK = register(
            "bio_nutrition_tank",
            ErErBlocks.BIO_NUTRITION_TANK,
            BioNutritionTankBlockEntity::new
    );
    public static final RegistryObject<BlockEntityType<BioControllerBlockEntity>> BIO_CONTROLLER = register(
            "bio_controller",
            ErErBlocks.BIO_CONTROLLER,
            BioControllerBlockEntity::new
    );
    public static final RegistryObject<BlockEntityType<BioConnectorBlockEntity>> BIO_CONNECTOR = register(
            "bio_connector",
            ErErBlocks.BIO_CONNECTOR,
            BioConnectorBlockEntity::new
    );
    public static final RegistryObject<BlockEntityType<BioCameraBlockEntity>> BIO_CAMERA = register(
            "bio_camera",
            ErErBlocks.BIO_CAMERA,
            BioCameraBlockEntity::new
    );

    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(
            String name,
            RegistryObject<Block> block,
            BlockEntityType.BlockEntitySupplier<T> supplier
    ) {
        Type<?> type = Util.fetchChoiceType(References.BLOCK_ENTITY, name);
        return REGISTRY.register(name, () -> BlockEntityType.Builder.of(supplier, block.get()).build(type));
    }
}
