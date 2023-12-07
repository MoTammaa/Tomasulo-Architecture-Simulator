package caches;

import java.util.ArrayList;
import java.util.List;

public class DataCache extends Cache{
    private final String[] data;
    private final boolean DISPLAY_EMPTY_BLOCKS = false;

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
            if (!DISPLAY_EMPTY_BLOCKS && data[i].equals("0")) continue;
            sb.append("@").append(i).append(": ").append(data[i]).append("\n");
        }
        return sb.toString();
    }
@Override
    public String[][] getTableData() {
        List<String[]> ret = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            if (!DISPLAY_EMPTY_BLOCKS && data[i].equals("0")) continue;
            ret.add(new String[]{String.valueOf(i), data[i]});
        }
        return ret.toArray(new String[0][0]);
    }
}
