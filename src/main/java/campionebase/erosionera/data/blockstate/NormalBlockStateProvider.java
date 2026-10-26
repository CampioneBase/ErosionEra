package campionebase.erosionera.data.blockstate;

import campionebase.erosionera.ErosionEra;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

/**
 * 核心模组的通用方块状态生成器。
 * 只会处理命名空间为 erosionera 的方块，附属模组的方块由各自的生成器负责。
 */
public class NormalBlockStateProvider extends BlockStateProvider {
    public NormalBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, ErosionEra.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        // 核心模组当前的方块内容（侵蚀 / 魔力相关）在此注册
    }

    /** 生成 六面同材质 的方块及其物品模型 */
    protected void simpleBlock(RegistryObject<Block> registryObject){
        Block block = registryObject.get();
        String name = registryObject.getId().getPath();
        ModelFile model = models()
                .getBuilder(name)
                .parent(models().getExistingFile(mcLoc("block/cube_all")))
                .texture("all", "block/" + name)
                .texture("particle", "block/" + name)
                .renderType(mcLoc("solid"));
        simpleBlockWithItem(block, model);

        itemModels().getBuilder(name).parent(model);
    }

    /** 复用原版已有模型的方块及其物品模型 */
    protected void copyBlock(RegistryObject<Block> registryObject, ResourceLocation copied){
        Block block = registryObject.get();
        String name = registryObject.getId().getPath();

        ModelFile model = models()
                .getBuilder(name)
                .parent(models().getExistingFile(copied));

        simpleBlock(block, model);
        itemModels().getBuilder(name).parent(model);
    }

    @Override
    public @NotNull String getName() {
        return super.getName() + "(Simple)";
    }
}
