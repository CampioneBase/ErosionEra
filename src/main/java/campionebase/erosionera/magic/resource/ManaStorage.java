package campionebase.erosionera.magic.resource;

import net.minecraft.nbt.IntTag;
import net.minecraft.util.Mth;

public class ManaStorage implements IManaStorage {
    /**
     * 空池
     */
    public static final IManaStorage EMPTY = new IManaStorage() {
        @Override
        public int capacity() {
            return 0;
        }

        @Override
        public int receive(int amount, boolean simulate) {
            return 0;
        }

        @Override
        public int mana() {
            return 0;
        }

        @Override
        public int extract(int amount, boolean simulate) {
            return 0;
        }

        @Override
        public IntTag serializeNBT() {
            return IntTag.valueOf(0);
        }

        @Override
        public void deserializeNBT(IntTag nbt) {
        }

        @Override
        public String toString() {
            return "ManaStorage{Empty}";
        }
    };

    /**
     * 创造池
     */
    public static final IManaStorage CREATIVE = new IManaStorage() {
        @Override
        public int capacity() {
            return Integer.MAX_VALUE;
        }

        @Override
        public int receive(int amount, boolean simulate) {
            return amount;
        }

        @Override
        public int mana() {
            return Integer.MAX_VALUE;
        }

        @Override
        public int extract(int amount, boolean simulate) {
            return amount;
        }

        @Override
        public IntTag serializeNBT() {
            return IntTag.valueOf(Integer.MAX_VALUE);
        }

        @Override
        public void deserializeNBT(IntTag nbt) {}

        @Override
        public String toString() {
            return "ManaStorage{Creative}";
        }
    };

    public static IManaStorage of(int capacity){ return of(capacity, 0); }
    public static IManaStorage of(int capacity, int init) {
        if (capacity < 0) throw new IllegalArgumentException("Capacity can NOT be negative");
        if (init < 0) throw new IllegalArgumentException("Mana can NOT be negative");
        if (capacity == 0) return EMPTY;
        return new ManaStorage(capacity, Math.min(init, capacity));
    }

    private int mana;
    private final int capacity;
    private ManaStorage(int capacity, int init) {
        this.capacity = capacity;
        this.mana = init;
    }

    @Override
    public int capacity() {
        return this.capacity;
    }

    @Override
    public int receive(int amount, boolean simulate) {
        int value = Mth.clamp(amount, 0, this.capacity - this.mana);
        this.mana += value;
        return value;
    }

    @Override
    public int mana() {
        return this.mana;
    }

    @Override
    public int extract(int amount, boolean simulate) {
        int value = Mth.clamp(amount, 0, this.mana);
        if (!simulate) this.mana -= value;
        return value;
    }

    @Override
    public IntTag serializeNBT(){
        return IntTag.valueOf(this.mana());
    }

    @Override
    public void deserializeNBT(IntTag nbt) {
        this.mana = nbt.getAsInt();
    }

    @Override
    public String toString() {
        return "ManaStorage{" +
                "capacity=" + this.capacity +
                "mana=" + this.mana +
                "}";
    }
}
