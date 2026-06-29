package campionebase.erosionera.init;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.data.lang.Translation;
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
    public static final RegistryObject<Item> BIOMETAL = REGISTRY.register("biometal", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> BIO_CONTROLLER_BED = block(ErErBlocks.BIO_CONTROLLER_BED);

    private static RegistryObject<Item> block(RegistryObject<Block> block) {
        return block(block, new Item.Properties());
    }

    private static RegistryObject<Item> block(RegistryObject<Block> block, Item.Properties properties) {
        return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), properties));
    }
}
