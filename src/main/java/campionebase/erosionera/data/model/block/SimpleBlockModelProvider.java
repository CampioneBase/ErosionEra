package campionebase.erosionera.data.model.block;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.registry.ErErBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class SimpleBlockModelProvider extends BlockModelProvider {
    public SimpleBlockModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, ErosionEra.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        cubeBlock(ErErBlocks.BIO_CONTROLLER);
        cubeBlock(ErErBlocks.BIO_NUTRITION_TANK);
    }

    private void cubeBlock(RegistryObject<Block> registryObject){
        String name = registryObject.getId().getPath();
        getBuilder(name)
                .parent(getExistingFile(mcLoc("block/cube_all")))
                .texture("all","block/" + name)
                .texture("particle", "block/" + name)
                .renderType(mcLoc("solid"));
    }
}
