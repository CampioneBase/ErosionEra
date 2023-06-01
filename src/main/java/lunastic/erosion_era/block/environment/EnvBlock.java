package lunastic.erosion_era.block.environment;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * 环境方块总类
 * @author Lunastic
 */
public class EnvBlock extends Block {
    // 添加布尔值属性 是否为原生方块
    // 以环境方式创建方块时为 true 其他方式为 false
    public static BooleanProperty ORIGINAL = BooleanProperty.of("original");

    public EnvBlock(Block.Settings settings) {
        super(settings);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ORIGINAL);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        // 放置时设置origin为false
        return this.getDefaultState().with(EnvBlock.ORIGINAL, false);
    }
}
