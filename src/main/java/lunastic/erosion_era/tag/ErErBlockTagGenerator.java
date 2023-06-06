package lunastic.erosion_era.tag;

import lunastic.erosion_era.init.ErErBlocks;
import lunastic.erosion_era.init.ErErTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;

public class ErErBlockTagGenerator extends FabricTagProvider.BlockTagProvider {

    public ErErBlockTagGenerator(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    protected void generateTags() {
        // 侵蚀环境方块标签
        this.getOrCreateTagBuilder(ErErTags.EROSION_ENV)
        .add(ErErBlocks.SHIMMER_CORE)
        .add(ErErBlocks.SHIMMER_PILLAR)

        ;
    }

}
