package lunastic.erosion_era.block.environment;

import lunastic.erosion_era.block.util.Erodible;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class ShimmerPillarBlock extends EnvBlock implements Erodible
{
    public ShimmerPillarBlock() {
        super(FabricBlockSettings
                .of(Material.METAL)
                .luminance(Erodible.getLuminanceFunctionWithEE(6))
        );
    }


    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);
        if(world.getBlockState(pos.up()).isAir()){
            world.addParticle(ParticleTypes.AMBIENT_ENTITY_EFFECT,
                    true,
                    pos.getX() + random.nextFloat(), pos.getY(), pos.getZ() + random.nextFloat(),
                    random.nextFloat(), random.nextFloat() + 1f, random.nextFloat()
            );
        }
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        Erodible.super.appendProperties(builder);
    }
}
