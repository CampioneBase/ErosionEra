package campionebase.erosionera.api;

import org.jetbrains.annotations.Nullable;

public interface IBioMachineController<M extends IBioMachine> extends IBioMachineAccessor<M> {
    void setCamera(M camera);
    @Nullable
    IBioController getController();
    @Nullable
    IBioCore getCore();
}
