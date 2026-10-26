package campionebase.erosionera.block.entity;

import campionebase.erosionera.recipe.RuneRecipe;
import campionebase.erosionera.registry.ErErBlockEntities;
import campionebase.erosionera.registry.ErErRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * 符文合成台方块实体。
 *
 * <p><b>槽位模型</b>（也是 {@link RuneRecipe#matches} 的输入约定）：
 * <ul>
 *   <li>槽 0：核心物品（恰好 1 个）</li>
 *   <li>槽 1..14：合成材料（每份 1 个，槽位顺序无意义）</li>
 * </ul>
 *
 * <p>交互与渲染都按这个模型分工：核心物品放在槽 0，材料依次占用后面的空槽。
 */
public class RuneCraftingTableBlockEntity extends BlockEntity implements Container {

    /** 槽 0 = 核心物品，其余 = 材料。 */
    public static final int CORE_SLOT = 0;
    /** 总槽位数。 */
    public static final int SLOT_COUNT = 15;

    // ---- 核心物品命中区域：顶面上 (5,5) ~ (10,10) 像素（16×16 面）----
    public static final float CORE_REGION_MIN = 5.0F / 16.0F;
    public static final float CORE_REGION_MAX = 11.0F / 16.0F;

    private final NonNullList<ItemStack> stacks = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);

    public RuneCraftingTableBlockEntity(BlockPos pos, BlockState blockState) {
        super(ErErBlockEntities.RUNE_CRAFTING_TABLE.get(), pos, blockState);
    }

    // ------------------------------------------------------------------ 命中区域

    /**
     * 顶面上的本地坐标（0..1）是否落在核心物品的命中区域内。
     *
     * <p>方块与渲染器共用这个判定，避免"高亮的地方"和"实际能放的地方"不一致。
     */
    public static boolean isCoreRegion(double localX, double localZ) {
        return localX >= CORE_REGION_MIN && localX <= CORE_REGION_MAX
                && localZ >= CORE_REGION_MIN && localZ <= CORE_REGION_MAX;
    }

    // ------------------------------------------------------------------ 配方

    /**
     * 当前内容匹配到的符文台配方。
     *
     * <p>配方匹配由 {@link RuneRecipe#matches} 完成：只取非空槽、槽位顺序无关。
     */
    public Optional<RuneRecipe> getMatchingRecipe() {
        if (this.level == null) {
            return Optional.empty();
        }
        return this.level.getRecipeManager()
                .getRecipeFor(ErErRecipes.RUNE_TABLE_TYPE.get(), this, this.level);
    }

    // ------------------------------------------------------------------ 交互辅助

    /** 第一个空的材料槽；没有空槽返回 -1。 */
    public int firstEmptyMaterialSlot() {
        for (int slot = CORE_SLOT + 1; slot < SLOT_COUNT; slot++) {
            if (this.stacks.get(slot).isEmpty()) {
                return slot;
            }
        }
        return -1;
    }

    /** 最后一个非空的材料槽（取下材料时用，保证先取后放的直观顺序）；没有则 -1。 */
    public int lastFilledMaterialSlot() {
        for (int slot = SLOT_COUNT - 1; slot > CORE_SLOT; slot--) {
            if (!this.stacks.get(slot).isEmpty()) {
                return slot;
            }
        }
        return -1;
    }

    /** 目标槽能否再放入 1 个该物品（空槽）。 */
    public boolean canInsertInto(int slot, ItemStack stack) {
        if (slot < 0 || slot >= SLOT_COUNT || stack.isEmpty()) {
            return false;
        }
        ItemStack current = this.stacks.get(slot);
        return current.isEmpty();
    }

    /**
     * 放入 1 个。
     *
     * @return 是否成功；失败时不做任何修改
     */
    public boolean insertOne(int slot, ItemStack stack) {
        if (!this.canInsertInto(slot, stack)) {
            return false;
        }
        ItemStack current = this.stacks.get(slot);
        if (current.isEmpty()) {
            ItemStack placed = stack.copyWithCount(1);
            this.stacks.set(slot, placed);
        } else {
            current.grow(1);
        }
        this.setChanged();
        return true;
    }

    // ------------------------------------------------------------------ Container

    @Override
    public int getContainerSize() {
        return SLOT_COUNT;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.stacks) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        if (slot < 0 || slot >= SLOT_COUNT) {
            return ItemStack.EMPTY;
        }
        return this.stacks.get(slot);
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        ItemStack removed = ContainerHelper.removeItem(this.stacks, slot, amount);
        if (!removed.isEmpty()) {
            this.setChanged();
        }
        return removed;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        ItemStack removed = ContainerHelper.takeItem(this.stacks, slot);
        if (!removed.isEmpty()) {
            this.setChanged();
        }
        return removed;
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack itemStack) {
        if (slot < 0 || slot >= SLOT_COUNT) {
            return;
        }
        this.stacks.set(slot, itemStack);
        if (itemStack.getCount() > this.getMaxStackSize()) {
            itemStack.setCount(this.getMaxStackSize());
        }
        this.setChanged();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        this.stacks.clear();
        this.setChanged();
    }

    // ------------------------------------------------------------------ 存档

    @Override
    public void setChanged() {
        super.setChanged();
        // 立刻更新
        if (this.level != null && !this.level.isClientSide){
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.stacks);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.stacks.clear();
        ContainerHelper.loadAllItems(tag, this.stacks);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        this.saveAdditional(tag);
        return tag;
    }

    /** 供调试/后续合成逻辑读取：材料槽里所有非空物品。 */
    @Nullable
    public List<ItemStack> materialView() {
        List<ItemStack> list = new java.util.ArrayList<>();
        for (int slot = CORE_SLOT + 1; slot < SLOT_COUNT; slot++) {
            ItemStack stack = this.stacks.get(slot);
            if (!stack.isEmpty()) {
                list.add(stack);
            }
        }
        return list;
    }

    /** 核心槽当前物品。 */
    public ItemStack coreStack() {
        return this.stacks.get(CORE_SLOT);
    }
}
