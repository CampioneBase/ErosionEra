package campionebase.erosionera.init;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.item.ErosionDetector;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ErErItems {
    public static DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, ErosionEra.MODID);

    // item
    public static final RegistryObject<Item> EROSION_DETECTOR = REGISTRY.register("erosion_detector", ErosionDetector::new);

    // spawn egg
    public static final RegistryObject<Item> ERODED_WOLF_SPAWN_EGG = REGISTRY.register("eroded_wolf_spawn_egg",
            () -> new ForgeSpawnEggItem(ErErEntities.ERODED_WOLF, 0xffcccccc, 0xff660099, new Item.Properties()));


    private static RegistryObject<Item> block(RegistryObject<Block> block) {
        return block(block, new Item.Properties());
    }

    private static RegistryObject<Item> block(RegistryObject<Block> block, Item.Properties properties) {
        return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), properties));
    }

}
