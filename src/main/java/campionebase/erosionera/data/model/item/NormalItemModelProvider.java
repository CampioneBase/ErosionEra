package campionebase.erosionera.data.model.item;

import campionebase.erosionera.ErosionEra;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

/**
 * 核心模组的通用物品模型生成器。
 */
public class NormalItemModelProvider extends ItemModelProvider {
    public NormalItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, ErosionEra.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        // 核心模组当前的物品内容（侵蚀 / 魔力相关）在此注册
    }

    /** 复用原版已有贴图的物品模型 */
    protected ItemModelBuilder copyItem(final RegistryObject<Item> registryObject, String copied){
        String name = registryObject.getId().getPath();
        return withExistingParent(name, mcLoc("item/generated"))
                .texture("layer0", mcLoc("item/" + copied));
    }

    /** 使用自身贴图的物品模型 */
    protected ItemModelBuilder simpleItem(final RegistryObject<Item> registryObject){
        String name = registryObject.getId().getPath();
        return withExistingParent(name, mcLoc("item/generated"))
                .texture("layer0", "item/" + name);
    }

    @Override
    public @NotNull String getName() {
        return super.getName() + "(normal)";
    }
}
