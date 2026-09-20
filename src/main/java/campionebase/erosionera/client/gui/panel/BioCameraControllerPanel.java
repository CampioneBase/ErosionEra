package campionebase.erosionera.client.gui.panel;

import campionebase.erosionera.api.BioMachineData;
import campionebase.erosionera.api.IBioCamera;
import campionebase.erosionera.api.IBioCore;
import campionebase.erosionera.api.IBioMachineController;
import campionebase.erosionera.blockentity.BioCameraBlockEntity;
import campionebase.erosionera.network.BioMachineryNetwork;
import campionebase.erosionera.network.packet.rr.BioCameraOccupationPacket;
import campionebase.erosionera.registry.BioMachineTypes;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

public class BioCameraControllerPanel extends BioCameraPanel {
    private final IBioMachineController<IBioCamera> controller;

    public BioCameraControllerPanel(Screen screen, IBioMachineController<IBioCamera> controller) {
        super(BioMachineTypes.CAMERA.get(), screen);
        this.controller = controller;
    }

    @Override
    protected void select(int index) {
        this.requestSelecting(index);
    }

    private void requestSelecting(int index){
        if (this.selectedIndex == index) return;
        IBioCore core = this.controller.getCore();
        if (core == null) return;
        // 向服务器请求具体的摄像机占用
        BlockPos oldPos = null, newPos = null;
        IBioCamera current = this.controller.getMachine();
        if (current != null)
            oldPos = current.getBlockPos();
        if (index > -1 && index < this.entries.size())
            newPos = this.entries.get(index).pos();
        BioMachineryNetwork.INSTANCE.sendToServer(new BioCameraOccupationPacket.Request(oldPos, newPos, core.getBlockPos()));
    }

    public void confirmSelecting(@Nullable IBioCamera camera){
        this.controller.setMachine(camera);
        if (camera == null) {
            super.select(-1);
            return;
        }
        for (int i = 0; i < this.entries.size(); i++) {
            if (camera.getBlockPos().equals(this.entries.get(i).pos())) {
                super.select(i);
                return;
            }
        }
        super.select(-1);
    }

    public void markSelection(BlockPos pos, String mark){
        for (int i = 0; i < this.entries.size(); i++) {
            BioMachineData data = this.getSelectedData(i);
            assert data != null : "Null data selection." +
                    " Type: " + this.type.getDescriptionId() +
                    ", Index: " + i +
                    ", Size: " + this.entries.size();
            if (pos.equals(data.pos())) {
                CompoundTag dataTag = data.customData();
                if (!dataTag.contains(BioCameraBlockEntity.TAG_OCCUPIER)){
                    dataTag.putString(BioCameraBlockEntity.TAG_OCCUPIER, mark);
                }
                return;
            }
        }
    }

    @Override
    protected boolean isSelectionValid(int index) {
        if (index == -1) return true;
        if (index < -1 || index >= this.entries.size()) return false;
        BioMachineData data = this.getSelectedData(index);
        assert data != null : "Null data selection." +
                " Type: " + this.type.getDescriptionId() +
                ", Index: " + index +
                ", Size: " + this.entries.size();
        CompoundTag tag = data.customData();
        return !tag.contains(BioCameraBlockEntity.TAG_OCCUPIER);
    }
}
