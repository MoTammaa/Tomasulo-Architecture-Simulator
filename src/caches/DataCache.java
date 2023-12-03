package caches;

public class DataCache extends Cache{
    private String[] data;

    public DataCache(int size) {
        this.size = size;
        this.data = new String[size];
        for (int i = 0; i < size; i++) {
            data[i] = "0";
        }
    }

    @Override
    public String M(int address) {
        return data[address];
    }

    @Override
    public String M(String address) {
        return data[Integer.parseInt(address)];
    }

    public void setM(String address, String data) {
        this.data[Integer.parseInt(address)] = data;
    }

    public void setM(int address, String data) {
        this.data[address] = data;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Data Cache:\n");
        for (int i = 0; i < size; i++) {
            sb.append("@").append(i).append(": ").append(data[i]).append("\n");
        }
        return sb.toString();
    }
}
