package reservationStations;

import instruction.Instruction;

public class LoadStoreBuffer {
    private final String bufferName;
    private boolean isOccupied=false;
    private String address;
    private String fu;
    private String Q;
    private Instruction instruction;

    public LoadStoreBuffer(String bufferName) {
        this.bufferName = bufferName;
        if (bufferName.startsWith("L")) {
            this.Q = "0";
        }
    }

    public String getName() {
        return bufferName;
    }
    public void issueInstruction(Instruction instruction) {
        this.instruction = instruction;
        this.isOccupied=true;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public boolean isReady() {
        return isOccupied && fu != null && address != null && Q.equals("0");
    }

    public void occupy() {
        isOccupied = true;
    }

    public void release() {
        isOccupied = false;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setFu(String fu) {
        this.fu = fu;
    }

    public String getAddress() {
        return address;
    }

    public String getFu() {
        return fu;
    }

    public Instruction getInstruction() {
        return instruction;
    }

    @Override
    public String toString() {
        return "LoadStoreBuffer{" +
                "bufferName='" + bufferName + '\'' +
                ", isOccupied=" + isOccupied +
                ", address='" + address + '\'' +
                ", fu='" + fu + '\'' +
                ", Q='" + Q + '\'' +
                ", instruction=" + instruction +
                '}';
    }
}
