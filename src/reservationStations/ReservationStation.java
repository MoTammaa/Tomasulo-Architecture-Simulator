package reservationStations;

import instruction.Instruction;

public class ReservationStation {
    private String reservationStationName;
    private boolean isOccupied;
    private String instructionType;
    private String vj;
    private String vk;
    private String Qj;
    private String Qk;
    private Instruction instruction;

    public void issueInstruction(Instruction instruction) {
        this.instruction = instruction;
        this.isOccupied=true;
    }
    
    public boolean isOccupied() {
        return isOccupied;
    }

    public void occupy() {
        this.isOccupied = true;
    }

    public void release() {
        this.isOccupied = false;
    }
}
