package reservationStations;

import instruction.Instruction;

public class ReservationStation {
    private String reservationStationName;
    private boolean isOccupied=false;
    private String instructionType;
    private String Vj;
    private String Vk;
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
    @Override
    public String toString() {
        return "ReservationStation{" +
                "reservationStationName='" + reservationStationName + '\'' +
                ", isOccupied=" + isOccupied +
                ", instructionType='" + instructionType + '\'' +
                ", Vj='" + Vj + '\'' +
                ", Vk='" + Vk + '\'' +
                ", Qj='" + Qj + '\'' +
                ", Qk='" + Qk + '\'' +
                ", instruction=" + instruction +
                '}';
    }
}
