package campionebase.erosionera.client.gui.panel;

import campionebase.erosionera.api.IBioCamera;
import campionebase.erosionera.api.IBioCore;
import campionebase.erosionera.api.IBioMachineController;
import campionebase.erosionera.network.BioMachineryNetwork;
import campionebase.erosionera.network.packet.BioCameraOccupationPacket;
import campionebase.erosionera.registry.BioMachineTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class CameraControllerPanel extends CameraPanel{
    private final IBioMachineController<IBioCamera> controller;

    public CameraControllerPanel(Level level, IBioMachineController<IBioCamera> controller) {
        super(BioMachineTypes.CAMERA.get(), level);
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
        IBioCamera current = this.controller.getCamera();
        if (current != null)
            oldPos = current.getBlockPos();
        if (index > -1 && index < this.entries.size())
            newPos = this.entries.get(index).pos();
        BioMachineryNetwork.INSTANCE.sendToServer(new BioCameraOccupationPacket.Request(oldPos, newPos, core.getBlockPos()));
    }

    public void confirmSelecting(@Nullable IBioCamera camera){
        this.controller.setCamera(camera);
        if (camera == null) {
            super.select(-1);
            return;
        }
        for (int i = 0; i < this.entries.size(); i++) {
            if (camera.getBlockPos().equals(this.entries.get(i).pos())) {
                super.select(i);
            }
        }
        super.select(-1);
    }
}
