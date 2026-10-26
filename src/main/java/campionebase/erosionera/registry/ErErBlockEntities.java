package campionebase.erosionera.registry;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.block.entity.RuneCraftingTableBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ErErBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ErosionEra.MODID);
    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(
            String name,
            RegistryObject<? extends Block> block,
            BlockEntityType.BlockEntitySupplier<T> supplier
    ) {
        return BLOCK_ENTITIES.register(name, () -> BlockEntityType.Builder.of(supplier, block.get()).build(null));
    }

    public static final RegistryObject<BlockEntityType<RuneCraftingTableBlockEntity>> RUNE_CRAFTING_TABLE = register(
            "rune_crafting_entity",
            ErErBlocks.RUNE_CRAFTING_TABLE,
            RuneCraftingTableBlockEntity::new
    );



}
