package campionebase.erosionera.blockentity;

import campionebase.erosionera.api.IBioMachine;
import campionebase.erosionera.api.IBioStorage;
import campionebase.erosionera.api.render.BioMachineRenderable;
import campionebase.erosionera.api.render.BioMachineRenderers;
import campionebase.erosionera.api.render.PanelRendererFactory;
import campionebase.erosionera.client.gui.panel.PanelRenderers;
import campionebase.erosionera.registry.ErErBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BioBinBlockEntity extends AbstractBioStorageBlockEntity {

    public BioBinBlockEntity(BlockPos pos, BlockState blockState) {
        super(ErErBlockEntities.BIO_BIN.get(), pos, blockState);
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return ErErBlockEntities.BIO_BIN.getId();
    }

    @Override
    public CompoundTag getCustomData() {
        CompoundTag tag = super.getCustomData();

        return tag;
    }

    public static PanelRendererFactory getRendererFactory() {
        return PanelRenderers.DEFAULT;
    }
}
