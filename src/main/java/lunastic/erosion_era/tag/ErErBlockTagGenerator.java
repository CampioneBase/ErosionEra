package lunastic.erosion_era.tag;

import lunastic.erosion_era.init.Blocks;
import lunastic.erosion_era.init.Tags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;

public class ErErBlockTagGenerator extends FabricTagProvider.BlockTagProvider {

    public ErErBlockTagGenerator(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    protected void generateTags() {
        // 侵蚀环境方块标签
        this.getOrCreateTagBuilder(Tags.EROSION_ENV)
        .add(Blocks.SHIMMER_CORE)
        .add(Blocks.SHIMMER_PILLAR)

        ;
    }

}
