package campionebase.erosionera.data.model.block;

import campionebase.erosionera.ErosionEra;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

public class BedBlockModelProvider extends BlockModelProvider {
    public BedBlockModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, ErosionEra.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        // 头模型
        getBuilder("bio_controller_bed_head")
                .parent(getExistingFile(mcLoc("block/block")))
                .texture("particle", modLoc("block/bio_controller_bed/side_left"))
                .texture("top", modLoc("block/bio_controller_bed/head"))
                .texture("bottom", modLoc("block/bio_controller_bed/button"))
                .texture("side_left", modLoc("block/bio_controller_bed/side_left"))
                .texture("side_right", modLoc("block/bio_controller_bed/side_right"))
                .texture("side", modLoc("block/bio_controller_bed/side"))
                .element()
                    .from(0, 0, 0)
                    .to(16, 8, 16)
                    .face(Direction.UP).uvs(0, 0, 16, 16).texture("#top").end()
                    .face(Direction.DOWN).uvs(0, 0, 16, 16).texture("#bottom").end()
                    .face(Direction.NORTH).uvs(0, 8, 16, 16).texture("#side").end()
                    .face(Direction.WEST).uvs(0, 8, 16, 16).texture("#side_left").end()
                    .face(Direction.EAST).uvs(0, 8, 16, 16).texture("#side_right").end()
                .end();

        // 脚模型
        getBuilder("bio_controller_bed_foot")
                .parent(getExistingFile(mcLoc("block/block")))
                .texture("particle", modLoc("block/bio_controller_bed/side_left"))
                .texture("top", modLoc("block/bio_controller_bed/foot"))
                .texture("bottom", modLoc("block/bio_controller_bed/button"))
                .texture("side_left", modLoc("block/bio_controller_bed/side_left"))
                .texture("side_right", modLoc("block/bio_controller_bed/side_right"))
                .texture("side", modLoc("block/bio_controller_bed/side"))
                .element()
                    .from(0, 0, 0)
                    .to(16, 8, 16)
                    .face(Direction.UP).uvs(0, 0, 16, 16).texture("#top").end()
                    .face(Direction.DOWN).uvs(0, 0, 16, 16).texture("#bottom").end()
                    .face(Direction.SOUTH).uvs(0, 8, 16, 16).texture("#side").end()
                    .face(Direction.WEST).uvs(0, 8, 16, 16).texture("#side_left").end()
                    .face(Direction.EAST).uvs(0, 8, 16, 16).texture("#side_right").end()
                .end();
    }

    @Override
    public @NotNull String getName() {
        return super.getName() + "(Bed)";
    }
}
