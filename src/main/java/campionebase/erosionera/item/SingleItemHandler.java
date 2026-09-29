package campionebase.erosionera.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class SingleItemHandler implements IItemHandler, INBTSerializable<CompoundTag> {
    public static final int DEFAULT_SLOT_LIMIT = 65535;
    private static final String TAG_ITEM = "item";
    private static final String TAG_COUNT = "count";
    private static final String TAG_NBT = "nbt";

    private final int slotLimit;
    private boolean locked = false;

    private Item currentItem = Items.AIR;
    private int itemCount;
    @Nullable
    private CompoundTag nbt = null;

    public SingleItemHandler() {
        this(DEFAULT_SLOT_LIMIT);
    }

    public SingleItemHandler(int slotLimit) {
        this.slotLimit = Math.max(1, slotLimit);
    }

    @Override
    public int getSlots() {
        return 1;
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        if (slot != 0 || this.currentItem == Items.AIR || this.itemCount <= 0) {
            return ItemStack.EMPTY;
        }
        return this.createStack(this.itemCount);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (slot != 0 || stack.isEmpty() || !this.isItemValid(slot, stack)) {
            return stack.copy();
        }

        if (this.currentItem == Items.AIR) {
            if (!simulate) {
                this.currentItem = stack.getItem();
                this.nbt = stack.hasTag() ? stack.getTag().copy() : null;
            }
            return this.insertCount(stack, simulate);
        }

        return this.insertCount(stack, simulate);
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot != 0 || amount <= 0 || this.currentItem == Items.AIR || this.itemCount <= 0) {
            return ItemStack.EMPTY;
        }

        int extracted = Math.min(amount, this.itemCount);
        ItemStack result = this.createStack(extracted);
        if (!simulate) {
            this.itemCount -= extracted;
            this.onChanged();
        }
        return result;
    }

    @Override
    public int getSlotLimit(int slot) {
        return slot == 0 ? this.slotLimit : 0;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        if (slot != 0 || stack.isEmpty()) {
            return false;
        }
        if (this.currentItem == Items.AIR) {
            return true;
        }
        return stack.is(this.currentItem) && Objects.equals(stack.getTag(), this.nbt);
    }

    public @NotNull Item getStoredItem() {
        return this.currentItem;
    }

    public int getStoredCount() {
        return this.itemCount;
    }

    @Nullable
    public CompoundTag getStoredNbt() {
        return this.nbt == null ? null : this.nbt.copy();
    }

    public void setStack(@NotNull ItemStack stack) {
        if (stack.isEmpty()) {
            if (this.currentItem != Items.AIR || this.itemCount != 0 || this.nbt != null) {
                this.currentItem = Items.AIR;
                this.itemCount = 0;
                this.nbt = null;
                this.onChanged();
            }
            return;
        }

        this.currentItem = stack.getItem();
        this.itemCount = Math.min(stack.getCount(), this.slotLimit);
        this.nbt = stack.hasTag() ? stack.getTag().copy() : null;
        this.onChanged();
    }

    public @NotNull CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        if (this.currentItem == Items.AIR) {
            return tag;
        }

        ResourceLocation id = ForgeRegistries.ITEMS.getKey(this.currentItem);
        if (id == null) {
            return tag;
        }
        tag.putString(TAG_ITEM, id.toString());
        tag.putInt(TAG_COUNT, this.itemCount);
        if (this.nbt != null) {
            tag.put(TAG_NBT, this.nbt.copy());
        }
        return tag;
    }

    public void deserializeNBT(@NotNull CompoundTag tag) {
        this.currentItem = Items.AIR;
        this.itemCount = 0;
        this.nbt = null;

        if (!tag.contains(TAG_ITEM, Tag.TAG_STRING)) {
            return;
        }

        ResourceLocation id = ResourceLocation.tryParse(tag.getString(TAG_ITEM));
        Item item = id == null ? null : ForgeRegistries.ITEMS.getValue(id);
        if (item == null || item == Items.AIR) {
            return;
        }

        this.currentItem = item;
        this.itemCount = Math.max(0, Math.min(tag.getInt(TAG_COUNT), this.slotLimit));
        if (tag.contains(TAG_NBT, Tag.TAG_COMPOUND)) {
            this.nbt = tag.getCompound(TAG_NBT).copy();
        }

        this.onLoad();
    }

    private @NotNull ItemStack insertCount(@NotNull ItemStack stack, boolean simulate) {
        int accepted = Math.min(stack.getCount(), this.slotLimit - this.itemCount);
        if (!simulate && accepted > 0) {
            this.itemCount += accepted;
            this.onChanged();
        }

        if (accepted >= stack.getCount()) {
            return ItemStack.EMPTY;
        }
        return stack.copyWithCount(stack.getCount() - accepted);
    }

    private @NotNull ItemStack createStack(int count) {
        ItemStack stack = new ItemStack(this.currentItem, count);
        if (this.nbt != null) {
            stack.setTag(this.nbt.copy());
        }
        return stack;
    }

    protected void onLoad(){

    }

    protected void onChanged(){

    }
}
