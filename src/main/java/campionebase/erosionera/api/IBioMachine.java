package campionebase.erosionera.api;

import campionebase.erosionera.network.packet.BioControllerCommandPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public interface IBioMachine {
    BlockPos getBlockPos();
    @NotNull
    BioMachineType<? extends IBioMachine> getMachineType();

    default boolean equals(IBioMachine machine){
        if (machine == null) return false;
        return this.getBlockPos().equals(machine.getBlockPos());
    }

    default CompoundTag getCustomData(){
        CompoundTag tag = new CompoundTag();
        if (this instanceof BlockEntity be && be.hasLevel()) {
            tag.putBoolean("is_loaded", true);
        }
        return tag;
    }

    default void receiveCommand(BioControllerCommandPacket packet, ServerLevel level, BlockPos corePos){ }
}
