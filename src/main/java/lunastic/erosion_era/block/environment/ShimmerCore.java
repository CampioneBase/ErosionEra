package lunastic.erosion_era.block.environment;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class ShimmerCore extends EnvBlock {
    public ShimmerCore() {
        super(FabricBlockSettings
                .of(Material.METAL)
                .luminance(state -> 12)
        );
    }

}
