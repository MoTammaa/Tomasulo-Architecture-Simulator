package reservationStations;

import engine.Tomasulo;
import instruction.ITypes;
import instruction.Instruction;

public class ReservationStation {
    private final String reservationStationName;
    private boolean isOccupied=false;
    private ITypes instructionType;
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
        this.instructionType = instruction.getInstructionType();
        this.isOccupied=true;

        if (instructionType.toString().endsWith("I")) { // if instruction is immediate
            this.Qj = Tomasulo.getRegisterFile().getQ(instruction.getRs());
            if (this.Qj.equals("0"))
                this.Vj = Tomasulo.getRegisterFile().getRegister(instruction.getRs());
            this.Vk = instruction.getImmediateOffset();
            this.Qk = "0";
        } else if ( instructionType == ITypes.BNEZ){
            this.Qj = Tomasulo.getRegisterFile().getQ(instruction.getRs());
            if (this.Qj.equals("0"))
                this.Vj = Tomasulo.getRegisterFile().getRegister(instruction.getRs());
            this.Vk = "0";
            this.Qk = "0";
        } else {
            this.Qj = Tomasulo.getRegisterFile().getQ(instruction.getRs());
            this.Qk = Tomasulo.getRegisterFile().getQ(instruction.getRt());
            if (this.Qj.equals("0"))
                this.Vj = Tomasulo.getRegisterFile().getRegister(instruction.getRs());
            if (this.Qk.equals("0"))
                this.Vk = Tomasulo.getRegisterFile().getRegister(instruction.getRt());
        }

        Tomasulo.getRegisterFile().setRegisterStatus(instruction.getRd(), reservationStationName);

    }

    public void setInstructionType(ITypes instructionType) {
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

    public ITypes getInstructionType() {
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
        return   reservationStationName + "{" +
                "Busy=" + (isOccupied? 1 : 0) +
                ", Op='" + instructionType + '\'' +
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

    public void broadcastResult() {
        String result = instruction.execute(this);

        for (ReservationStation reservationStation : Tomasulo.getAddSubReservationStations()) {
            if (reservationStation.getQj().equals(reservationStationName)) {
                reservationStation.setQj("0");
                reservationStation.setVj(result);
            }
            if (reservationStation.getQk().equals(reservationStationName)) {
                reservationStation.setQk("0");
                reservationStation.setVk(result);
            }
        }

        for (ReservationStation reservationStation : Tomasulo.getMulDivReservationStations()) {
            if (reservationStation.getQj().equals(reservationStationName)) {
                reservationStation.setQj("0");
                reservationStation.setVj(result);
            }
            if (reservationStation.getQk().equals(reservationStationName)) {
                reservationStation.setQk("0");
                reservationStation.setVk(result);
            }
        }

        // now for the store buffers
        for (LoadStoreBuffer sBuffer : Tomasulo.getStoreBuffers()) {
            if (sBuffer.getQ().equals(reservationStationName)) {
                sBuffer.setQ("0");
                sBuffer.setFu(result);
            }
        }
    }
}
