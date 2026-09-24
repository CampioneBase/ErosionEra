package campionebase.erosionera.blockentity;

import campionebase.erosionera.api.render.PanelRendererFactory;
import campionebase.erosionera.client.gui.panel.PanelRenderers;
import campionebase.erosionera.registry.ErErBlockEntities;
import campionebase.erosionera.registry.ErErBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BioChestBlockEntity extends AbstractBioStorageBlockEntity implements MenuProvider, Container {
    private static final int SLOT_COUNT = 27;
    private int openCount = 0;
    private final ItemStackHandler inventory = new ItemStackHandler(SLOT_COUNT){
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            markStorageDirty();
        }
    };

    public BioChestBlockEntity(BlockPos pos, BlockState blockState) {
        super(ErErBlockEntities.BIO_CHEST.get(), pos, blockState);
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return ErErBlocks.BIO_CHEST.getId();
    }

    @Override
    public CompoundTag getCustomData() {
        return super.getCustomData();
    }

    public static PanelRendererFactory getRendererFactory(){
        return PanelRenderers.DEFAULT;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(TAG_INVENTORY, this.inventory.serializeNBT());
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        if (tag.contains(TAG_INVENTORY)) this.inventory.deserializeNBT(tag.getCompound(TAG_INVENTORY));
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        this.saveAdditional(tag);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private final LazyOptional<IItemHandler> itemHandlerCap = LazyOptional.of(() -> this.inventory);
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER){
            return this.itemHandlerCap.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.itemHandlerCap.invalidate();
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable(this.getMachineType().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory inventory, @NotNull Player player) {
        return ChestMenu.threeRows(windowId, inventory, this);
    }

    @Override
    public int getContainerSize() { return SLOT_COUNT; }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < SLOT_COUNT; i++) {
            if (!this.inventory.getStackInSlot(i).isEmpty()) return false;
        }
        return true;
    }

    @Override
    public @NotNull ItemStack getItem(int slot) { return this.inventory.getStackInSlot(slot); }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        return this.inventory.extractItem(slot, amount, false);
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        ItemStack itemStack = this.inventory.getStackInSlot(slot);
        if (itemStack.isEmpty()) return ItemStack.EMPTY;
        this.inventory.setStackInSlot(slot, ItemStack.EMPTY);
        return itemStack;
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack itemStack) {
        this.inventory.setStackInSlot(slot, itemStack);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        if (this.level == null || this.isRemoved()) return false;
        return player.distanceToSqr(this.getBlockPos().getCenter()) <= 64.0;
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < SLOT_COUNT; i++) this.inventory.setStackInSlot(i, ItemStack.EMPTY);
    }

    @Override
    public void startOpen(Player player) {
        if (!player.isSpectator()) {
            if (this.openCount++ == 0 && this.level != null){
                // TODO: 未来可在此切换 BlockState 的 OPEN 属性、播放开盖音效
                if (this.level instanceof ServerLevel serverLevel) {
                    serverLevel.playSound(null, this.getBlockPos(), SoundEvents.BARREL_OPEN, SoundSource.BLOCKS);
                }
            }
        }
    }

    @Override
    public void stopOpen(Player player) {
        if (!player.isSpectator()) {
            if (--this.openCount == 0 && this.level != null){
                // TODO: 音效、BlockState 复原
                if (this.level instanceof ServerLevel serverLevel){
                    serverLevel.playSound(null, this.getBlockPos(), SoundEvents.BARREL_CLOSE, SoundSource.BLOCKS);
                }
            }
        }
    }
}
