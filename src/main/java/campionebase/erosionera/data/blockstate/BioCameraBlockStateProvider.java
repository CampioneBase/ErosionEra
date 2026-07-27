package campionebase.erosionera.data.blockstate;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.block.BioCameraBlock;
import campionebase.erosionera.registry.ErErBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

public class BioCameraBlockStateProvider extends BlockStateProvider {
    public BioCameraBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, ErosionEra.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        Block block = ErErBlocks.BIO_CAMERA.get();
        String name = ErErBlocks.BIO_CAMERA.getId().getPath();

        ModelBuilder<?> modelBuilder = models().getBuilder(name)
                .parent(models().getExistingFile(mcLoc("block/block")))
                .texture("particle", modLoc("block/bio_camera"))
                .texture("top", modLoc("block/bio_camera"))
                .texture("side", modLoc("block/bio_camera_side"))
                .texture("bottom", modLoc("block/bio_machinery"));

        modelBuilder.element()
                .from(0, 0, 0).to(16, 4, 16)
                .allFaces((direction, face) -> {
                    switch (direction) {
                        case UP -> face.texture("#top").cullface(direction);
                        case DOWN -> face.texture("#bottom").cullface(direction);
                        default -> face.texture("#side").uvs(0, 12, 16, 16).cullface(direction);
                    }
                });
        modelBuilder.element()
                .from(5, 4, 5).to(11, 10, 11)
                .allFaces((direction, face) -> {
                    face.texture("#top").uvs(5, 5, 11, 11);
                });

        getVariantBuilder(block).forAllStates(blockState -> {
            Direction facing = blockState.getValue(BioCameraBlock.FACING);
            return ConfiguredModel.builder()
                    .modelFile(modelBuilder)
                    .rotationX(facing.equals(Direction.UP) ? 0 : 180)
                    .build();
        });

        itemModels().getBuilder(name).parent(modelBuilder);
    }

    @Override
    public @NotNull String getName() {
        return super.getName() + "(Bio Camera)";
    }
}
