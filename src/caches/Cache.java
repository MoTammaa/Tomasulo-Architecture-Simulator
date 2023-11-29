package caches;

public abstract class Cache {
    /* default */int size;

    public abstract Object M(int address);
    public abstract Object M(String address);


    public int getMaxSize() {
        return size;
    }


}
