package lunastic.erosion_era.block.environment;

import lunastic.erosion_era.init.ErErBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Material;

public class ShimmerCoreBlock extends EnvBlock {
    public ShimmerCoreBlock() {
        super(FabricBlockSettings
                .of(Material.METAL)
        );
    }
}
