package campionebase.erosionera.blockentity;

import campionebase.erosionera.api.IBioMachine;
import campionebase.erosionera.api.render.BioMachineRenderable;
import campionebase.erosionera.api.render.BioMachineRenderers;
import campionebase.erosionera.data.lang.TranslationKeys;
import campionebase.erosionera.item.SingleItemHandler;
import campionebase.erosionera.registry.ErErBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BioBinBlockEntity extends AbstractBioStorageBlockEntity {
    private final SingleItemHandler inventory = new SingleItemHandler(SingleItemHandler.DEFAULT_SLOT_LIMIT){
        @Override
        protected void onChanged() {
            setChanged();
            markStorageDirty();
        }
    };
    private final LazyOptional<IItemHandler> itemHandlerCap = LazyOptional.of(() -> this.inventory);

    public BioBinBlockEntity(BlockPos pos, BlockState blockState) {
        super(ErErBlockEntities.BIO_BIN.get(), pos, blockState);
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return ErErBlockEntities.BIO_BIN.getId();
    }

    @Override
    public @NotNull CompoundTag getCustomData() {
        CompoundTag tag = super.getCustomData();
        tag.put(TAG_INVENTORY, this.inventory.serializeNBT());
        return tag;
    }

    public static @NotNull BioMachineRenderable getRenderer(CompoundTag customData) {
        SingleItemHandler inventory = new SingleItemHandler();
        inventory.deserializeNBT(customData.getCompound(TAG_INVENTORY));

        Item item = inventory.getStoredItem();
        Component itemName = Component.translatable(TranslationKeys.BIO_MACHINE_EMPTY);
        if (item != Items.AIR) {
            ItemStack displayStack = new ItemStack(item);
            CompoundTag itemTag = inventory.getStoredNbt();
            if (itemTag != null) {
                displayStack.setTag(itemTag);
            }
            itemName = displayStack.getHoverName();
        }

        Component status = Component.translatable(
                TranslationKeys.BIO_MACHINE_BIN_CONTENT,
                itemName, inventory.getStoredCount(), SingleItemHandler.DEFAULT_SLOT_LIMIT);
        return BioMachineRenderers.selectedText(
                Component.translatable(TranslationKeys.BIO_MACHINE_NAMED_STATUS,
                        IBioMachine.getDisplayName(customData), status),
                BioMachineRenderers.NORMAL_COLOR,
                BioMachineRenderers.SELECTED_COLOR
        );
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(TAG_INVENTORY, this.inventory.serializeNBT());
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        if (tag.contains(TAG_INVENTORY)) {
            this.inventory.deserializeNBT(tag.getCompound(TAG_INVENTORY));
        }
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

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return this.itemHandlerCap.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.itemHandlerCap.invalidate();
    }
}
