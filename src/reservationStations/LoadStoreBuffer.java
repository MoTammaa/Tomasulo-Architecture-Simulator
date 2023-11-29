package reservationStations;

import instruction.Instruction;

public class LoadStoreBuffer {
    private String bufferName;
    private boolean isOccupied;
    private String address;
    private String fu;
    private Instruction instruction;
    
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
