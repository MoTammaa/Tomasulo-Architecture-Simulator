package instruction;

public class Instruction {
    private String instructionType;
    private String Rs;
    private String Rd;
    private String Rt;
    private String immediateOffset;
    private InstructionStatus instructionStatus;
    
    public String getInstructionType() {
        return instructionType;
    }

    public void setInstructionType(String instructionType) {
        this.instructionType = instructionType;
    }

    public String getRs() {
        return Rs;
    }

    public void setRs(String rs) {
        Rs = rs;
    }

    public String getRd() {
        return Rd;
    }

    public void setRd(String rd) {
        Rd = rd;
    }

    public String getRt() {
        return Rt;
    }

    public void setRt(String rt) {
        Rt = rt;
    }

    public String getImmediateOffset() {
        return immediateOffset;
    }

    public void setImmediateOffset(String immediateOffset) {
        this.immediateOffset = immediateOffset;
    }

    public InstructionStatus getInstructionStatus() {
        return instructionStatus;
    }

    public void setInstructionStatus(InstructionStatus instructionStatus) {
        this.instructionStatus = instructionStatus;
    }

    public String toString() {
        return "Type: " + instructionType + " Rs: " + Rs + " Rd: " + Rd + " Rt: " + Rt + " Immediate: " + immediateOffset;
    }
}
