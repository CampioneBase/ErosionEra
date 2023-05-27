package lunastic.erosion_era.block.environment;

import lunastic.erosion_era.init.ErErBlocks;
import net.minecraft.block.Material;

public class ShimmerCoreBlock extends EnvBlock {
    public ShimmerCoreBlock() {
        super(ErErBlocks.Settings
                .of(Material.METAL)
                .eroded()
        );
    }
}
