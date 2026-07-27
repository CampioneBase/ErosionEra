package campionebase.erosionera.data.model.item;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.registry.ErErItems;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

public class NormalItemModelProvider extends ItemModelProvider {
    public NormalItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, ErosionEra.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(ErErItems.BIO_METAL);

        copyItem(ErErItems.BIO_WIRE, "chain");
    }

    private ItemModelBuilder copyItem(final RegistryObject<Item> registryObject, String copied){
        String name = registryObject.getId().getPath();
        return withExistingParent(name, mcLoc("item/generated"))
                .texture("layer0", mcLoc("item/" + copied));
    }

    private ItemModelBuilder simpleItem(final RegistryObject<Item> registryObject){
        String name = registryObject.getId().getPath();
        return withExistingParent(name, mcLoc("item/generated"))
                .texture("layer0", modLoc("item/" + name));
    }

    @Override
    public @NotNull String getName() {
        return super.getName() + "(normal)";
    }
}
