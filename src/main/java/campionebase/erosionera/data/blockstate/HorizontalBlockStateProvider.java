package campionebase.erosionera.data.blockstate;

import campionebase.erosionera.ErosionEra;
import campionebase.erosionera.block.BioControllerBedBlock;
import campionebase.erosionera.registry.ErErBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

public class HorizontalBlockStateProvider extends BlockStateProvider {

    public HorizontalBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, ErosionEra.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        Block block = ErErBlocks.BIO_CONTROLLER_BED.get();

        ModelFile headModel = models().getExistingFile(modLoc("block/bio_controller_bed_head"));
        ModelFile footModel = models().getExistingFile(modLoc("block/bio_controller_bed_foot"));
        getVariantBuilder(block).forAllStates(state -> {
            Direction facing = state.getValue(HorizontalDirectionalBlock.FACING);
            BedPart part = state.getValue(BioControllerBedBlock.BED_PART);

            int yRot = switch (facing) {
                case EAST  -> 90;
                case SOUTH -> 180;
                case WEST  -> 270;
                default    -> 0; // NORTH
            };

            ModelFile model = (part == BedPart.HEAD) ? headModel : footModel;
            return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationY(yRot)
                    .build();
        });
    }

    @Override
    public @NotNull String getName() {
        return super.getName() + "(Horizontal)";
    }
}
