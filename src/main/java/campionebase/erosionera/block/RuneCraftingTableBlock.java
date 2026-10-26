package campionebase.erosionera.block;

import campionebase.erosionera.block.entity.RuneCraftingTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 符文合成台。
 *
 * <p><b>交互分区</b>（决定与哪个槽位互动）：
 * <ul>
 *   <li>顶面的 (5,5)~(10,10) 像素区域 → 核心物品槽（槽 0）</li>
 *   <li>顶面其余区域、以及四个侧面 → 合成材料槽（槽 1..14）</li>
 *   <li><b>底面不响应</b></li>
 * </ul>
 *
 * <p><b>交互语义</b>：空手 = 取下 1 个（直接进手）；手持物品 = 放入 1 个。
 */
public class RuneCraftingTableBlock extends Block implements EntityBlock {
    public static final VoxelShape BASE_SHAPE = Block.box(0, 0, 0, 16, 8, 16);

    public RuneCraftingTableBlock() {
        super(BlockBehaviour.Properties.of()
                .sound(SoundType.STONE)
                .requiresCorrectToolForDrops()
        );
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Override
    public @NotNull VoxelShape getShape(
            @NotNull BlockState blockState,
            @NotNull BlockGetter world,
            @NotNull BlockPos pos,
            @NotNull CollisionContext context) {
        return BASE_SHAPE;
    }

    @Override
    public @NotNull InteractionResult use(
            @NotNull BlockState blockState,
            @NotNull Level level,
            @NotNull BlockPos pos,
            @NotNull Player player,
            @NotNull InteractionHand hand,
            @NotNull BlockHitResult hit
    ) {
        // 底面不响应
        if (hit.getDirection() == Direction.DOWN) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (!(level.getBlockEntity(pos) instanceof RuneCraftingTableBlockEntity table)) {
            return InteractionResult.PASS;
        }

        boolean coreRegion = isCoreRegion(hit, pos);
        ItemStack held = player.getItemInHand(hand);

        boolean handled = held.isEmpty()
                ? this.takeOne(table, player, hand, coreRegion)
                : this.placeOne(table, player, hand, held, coreRegion);

        return handled ? InteractionResult.CONSUME : InteractionResult.PASS;
    }

    /** 命中点是否落在顶面的核心区域内（非顶面一律 false）。 */
    private static boolean isCoreRegion(BlockHitResult hit, BlockPos pos) {
        if (hit.getDirection() != Direction.UP) {
            return false;
        }
        double localX = hit.getLocation().x - pos.getX();
        double localZ = hit.getLocation().z - pos.getZ();
        return RuneCraftingTableBlockEntity.isCoreRegion(localX, localZ);
    }

    // ------------------------------------------------------------------ 取下

    private boolean takeOne(RuneCraftingTableBlockEntity table, Player player, InteractionHand hand, boolean coreRegion) {
        int slot;
        if (coreRegion) {
            slot = RuneCraftingTableBlockEntity.CORE_SLOT;
            if (table.getItem(slot).isEmpty()) {
                return false;
            }
        } else {
            slot = table.lastFilledMaterialSlot();
            if (slot < 0) {
                return false;
            }
        }
        ItemStack taken = table.removeItem(slot, 1);
        if (taken.isEmpty()) {
            return false;
        }
        player.setItemInHand(hand, taken);
        return true;
    }

    // ------------------------------------------------------------------ 放入

    private boolean placeOne(RuneCraftingTableBlockEntity table, Player player, InteractionHand hand,
                             ItemStack held, boolean coreRegion) {
        int slot = coreRegion ? RuneCraftingTableBlockEntity.CORE_SLOT : this.findMaterialSlotFor(table, held);
        if (slot < 0 || !table.insertOne(slot, held)) {
            return false;
        }
        if (!player.isCreative()){
            held.shrink(1);
        }
        if (held.isEmpty()) {
            player.setItemInHand(hand, ItemStack.EMPTY);
        }
        return true;
    }

    /**
     * 材料槽的落点：优先空槽；没有空槽就找同类且未满的槽。
     *
     * @return 可用槽位；没有则 -1
     */
    private int findMaterialSlotFor(RuneCraftingTableBlockEntity table, ItemStack held) {
        int empty = table.firstEmptyMaterialSlot();
        if (empty >= 0) {
            return empty;
        }
        for (int slot = RuneCraftingTableBlockEntity.CORE_SLOT + 1;
             slot < RuneCraftingTableBlockEntity.SLOT_COUNT; slot++) {
            ItemStack current = table.getItem(slot);
            if (ItemStack.isSameItemSameTags(current, held) && current.getCount() < current.getMaxStackSize()) {
                return slot;
            }
        }
        return -1;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new RuneCraftingTableBlockEntity(pos, state);
    }
}
