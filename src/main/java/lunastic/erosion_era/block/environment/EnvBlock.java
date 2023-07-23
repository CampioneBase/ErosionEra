package lunastic.erosion_era.block.environment;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import org.jetbrains.annotations.Nullable;

/**
 * 环境方块总类
 * @author Lunastic
 */
public class EnvBlock extends Block {

    public EnvBlock(Block.Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        // 放置时设置origin为false
        return this.getDefaultState();
    }
}
