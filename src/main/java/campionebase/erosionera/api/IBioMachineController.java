package campionebase.erosionera.api;

import org.jetbrains.annotations.Nullable;

public interface IBioMachineController<M extends IBioMachine> extends IBioMachineAccessor<M> {
    void setMachine(M machine);
    @Nullable
    IBioController getController();
    @Nullable
    IBioCore getCore();
}
