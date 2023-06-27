package lunastic.erosion_era.block.environment;

import lunastic.erosion_era.block.util.Erodible;
import lunastic.erosion_era.init.ErErStatusEffects;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;

public class ShimmerCore extends EnvBlock implements Erodible{
    private int ticks = 0;

    public ShimmerCore() {
        super(FabricBlockSettings
                .of(Material.METAL)
                .luminance(Erodible.getLuminanceFunctionWithEE(12))
        );
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if(ticks ++ % 10 == 0){
            Box box = new Box(pos).expand(10).stretch(0.0, world.getHeight(), 0.0);
            List<PlayerEntity> list = world.getNonSpectatingEntities(PlayerEntity.class, box);
            list.forEach(p -> p.addStatusEffect(new StatusEffectInstance(ErErStatusEffects.EROSION_EFFECT, 100, 1, true, true)));
        }
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        Erodible.super.appendProperties(builder);
    }
}
