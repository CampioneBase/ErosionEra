package campionebase.erosionera.registry;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.data.lang.Translation;
import campionebase.erosionera.item.BioWireItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@Translation(key = "item")
public class ErErItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(Registries.ITEM, ErosionEra.MODID);
    @Translation.ZH_CN("活体金属")
    public static final RegistryObject<Item> BIO_METAL = REGISTRY.register("bio_metal", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> BIO_CONTROLLER_BED = block(ErErBlocks.BIO_CONTROLLER_BED);
    @Translation.ZH_CN("活体管道")
    public static final RegistryObject<Item> BIO_WIRE = REGISTRY.register("bio_wire", BioWireItem::new);

    public static final RegistryObject<Item> BIO_CONTROLLER = block(ErErBlocks.BIO_CONTROLLER);
    public static final RegistryObject<Item> BIO_CONNECTOR = block(ErErBlocks.BIO_CONNECTOR);
    public static final RegistryObject<Item> BIO_NUTRITION_TANK = block(ErErBlocks.BIO_NUTRITION_TANK);
    public static final RegistryObject<Item> BIO_CAMERA = block(ErErBlocks.BIO_CAMERA);

    private static RegistryObject<Item> block(RegistryObject<Block> block) {
        return block(block, new Item.Properties());
    }

    private static RegistryObject<Item> block(RegistryObject<Block> block, Item.Properties properties) {
        return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), properties));
    }
}
