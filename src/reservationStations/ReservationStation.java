package reservationStations;

import instruction.Instruction;

public class ReservationStation {
    private final String reservationStationName;
    private boolean isOccupied=false;
    private String instructionType;
    private String Vj;
    private String Vk;
    private String Qj;
    private String Qk;
    private Instruction instruction;

    public ReservationStation(String reservationStationName) {
        this.reservationStationName = reservationStationName;
    }

    public void issueInstruction(Instruction instruction) {
        this.instruction = instruction;
        this.isOccupied=true;
    }

    public void setInstructionType(String instructionType) {
        this.instructionType = instructionType;
    }

    public void setVj(String vj) {
        Vj = vj;
    }

    public void setVk(String vk) {
        Vk = vk;
    }

    public void setQj(String qj) {
        Qj = qj;
    }

    public void setQk(String qk) {
        Qk = qk;
    }

    public String getReservationStationName() {
        return reservationStationName;
    }

    public String getInstructionType() {
        return instructionType;
    }

    public String getVj() {
        return Vj;
    }

    public String getVk() {
        return Vk;
    }

    public String getQj() {
        return Qj;
    }

    public String getQk() {
        return Qk;
    }

    public Instruction getInstruction() {
        return instruction;
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

    public boolean isReady() {
        return Qj!= null && Qk!=null && Qj.equals("0") && Qk.equals("0");
    }
}
