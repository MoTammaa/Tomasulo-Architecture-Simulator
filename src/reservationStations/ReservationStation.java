package reservationStations;

import engine.Tomasulo;
import instruction.ITypes;
import instruction.Instruction;
import registerFile.Register;

public class ReservationStation extends Station {
    private final String reservationStationName;
    private ITypes instructionType;
    private String Vj;
    private String Vk;
    private String Qj;
    private String Qk;

    public ReservationStation(String reservationStationName) {
        this.reservationStationName = this.name = reservationStationName;
    }

    public void issueInstruction(Instruction instruction) {
        this.instruction = instruction;
        this.instructionType = instruction.getInstructionType();
        this.isOccupied=true;
        this.Vj = null;
        this.Vk = null;
        this.Qj = null;
        this.Qk = null;

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
            Tomasulo.getRegisterFile().setBTrue();
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


    public void writeBack() {
        instruction.setWriteBack(Tomasulo.getCurrentCycle());
        if (Tomasulo.getRegisterFile().getRegisterStatus(instruction.getRd()).equals(reservationStationName) || instruction.getRd().equals("B")){
            Tomasulo.getRegisterFile().setRegisterValue(instruction.getRd(), instruction.execute(this));
            Tomasulo.getRegisterFile().setRegisterStatus(instruction.getRd(), "0");
        }

        if (instructionType == ITypes.BNEZ && Tomasulo.getRegisterFile().getRegister("B").equals("1")) {
            Tomasulo.getRegisterFile().setBFalse();
            Tomasulo.getInstructionCache().setPC(Integer.parseInt(instruction.getImmediateOffset()));
//            System.err.println("||||||||Branching to instruction ("+ Tomasulo.getInstructionCache().getCurrentInstructionIndex()+")"  + Tomasulo.getInstructionCache().getCurrentInstruction().toString() + ":: at cycle " + Tomasulo.getCurrentCycle());
        }
    }
}
