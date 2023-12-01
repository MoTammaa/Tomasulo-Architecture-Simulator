package reservationStations;

import instruction.Instruction;

public class LoadStoreBuffer {
    private String bufferName;
    private boolean isOccupied=false;
    private String address;
    private String fu;
    private Instruction instruction;

    public LoadStoreBuffer(String bufferName) {
        this.bufferName = bufferName;
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

    public void occupy() {
        isOccupied = true;
    }

    public void release() {
        isOccupied = false;
    }
}
