package instruction;

public class Instruction {
    private String instructionType;
    private String Rs;
    private String Rd;
    private int Rt;
    private int immediateOffset;
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

    public int getRt() {
        return Rt;
    }

    public void setRt(int rt) {
        Rt = rt;
    }

    public int getImmediateOffset() {
        return immediateOffset;
    }

    public void setImmediateOffset(int immediateOffset) {
        this.immediateOffset = immediateOffset;
    }

    public InstructionStatus getInstructionStatus() {
        return instructionStatus;
    }

    public void setInstructionStatus(InstructionStatus instructionStatus) {
        this.instructionStatus = instructionStatus;
    }

    public String toString() {
        // Provide a meaningful string representation of the instruction
        return "Type: " + instructionType + ", Rs: " + Rs + ", Rd: " + Rd + ", Rt: " + Rt + ", Immediate: " + immediateOffset;
    }
}
