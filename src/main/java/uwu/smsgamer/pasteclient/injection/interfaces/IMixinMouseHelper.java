package uwu.smsgamer.pasteclient.injection.interfaces;

public interface IMixinMouseHelper {
    /**
     * 0 none, 1 set, 2 add, 3 mult/div side
     * @param mode Mode for the type.
     */
    void setMode(int mode);

    void reset();

    void setSideX(int side);
    void setSideY(int side);

    void setX(int amount);
    void setY(int amount);

    void setAddX(int amount);
    void setAddY(int amount);

    void setMultX(double amount);
    void setDivX(double amount);
    void setMultY(double amount);
    void setDivY(double amount);
}
