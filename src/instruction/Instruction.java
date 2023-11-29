package instruction;

public class Instruction {
    private String instructionType;
    private String Rs;
    private String Rd;
    private int Rt;
    private int immediateOffset;
    private InstructionStatus instructionStatus;

    public Instruction(String instructionType) {
        this.instructionType = instructionType;
    }
    public String execute() {
        return "Executed";
    }
    @Override
    public String toString() {
        return "'" + instructionType + " " + Rs + " ," + Rd + " ," + Rt + " " + immediateOffset + "'" /*+ " " + instructionStatus*/;
    }
}
