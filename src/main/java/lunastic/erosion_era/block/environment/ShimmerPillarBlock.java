package lunastic.erosion_era.block.environment;

import lunastic.erosion_era.init.ErErProperties;
import lunastic.erosion_era.init.ErErRules;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.windows.POINT;

public class ShimmerPillarBlock extends EnvBlock {
    public ShimmerPillarBlock() {
        super(FabricBlockSettings
                .of(Material.METAL)
                .luminance(state -> state.get(ORIGINAL) ? 6 : 0)
        );
    }


    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);
        if(state.get(ORIGINAL) && world.getBlockState(pos.up()).isAir()){
            world.addParticle(ParticleTypes.AMBIENT_ENTITY_EFFECT,
                    true,
                    pos.getX() + random.nextFloat(), pos.getY(), pos.getZ() + random.nextFloat(),
                    random.nextFloat(), random.nextFloat() + 1f, random.nextFloat()
            );
        }
    }
}
