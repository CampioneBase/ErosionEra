package campionebase.erosionera.data.blockstate;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.registry.ErErBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

public class NormalBlockStateProvider extends BlockStateProvider {
    public NormalBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, ErosionEra.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(ErErBlocks.BIO_CONTROLLER);
        simpleBlock(ErErBlocks.BIO_NUTRITION_TANK);
        simpleBlock(ErErBlocks.BIO_REDSTONE);
    }

    private void simpleBlock(RegistryObject<Block> registryObject){
        Block block = registryObject.get();
        String name = registryObject.getId().getPath();
        ModelFile model = models()
                .getBuilder(name)
                .parent(models().getExistingFile(mcLoc("block/cube_all")))
                .texture("all","block/" + name)
                .texture("particle", "block/" + name)
                .renderType(mcLoc("solid"));
        simpleBlockWithItem(block, model);
    }

    @Override
    public @NotNull String getName() {
        return super.getName() + "(Simple)";
    }
}
