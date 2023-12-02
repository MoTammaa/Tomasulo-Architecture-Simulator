package instruction;

import engine.Tomasulo;

public class Instruction {
    private ITypes instructionType;
    private String Rs;
    private String Rd;
    private String Rt;
    private String immediateOffset;
    private InstructionStatus instructionStatus = new InstructionStatus();

    public void setIssue(int currentCycle) {
    	this.instructionStatus.setIssue(currentCycle);
    }
    public void startExecution(int currentCycle) {
        int totalCycles = 0;

        switch (instructionType) {
            case ADD:
                totalCycles = Tomasulo.ADD_CYCLES;
                break;
            case SUB:
                totalCycles = Tomasulo.SUB_CYCLES;
                break;
            case MUL:
            case MULI:
                totalCycles = Tomasulo.MUL_CYCLES;
                break;
            case DIV:
            case DIVI:
                totalCycles = Tomasulo.DIV_CYCLES;
                break;
            case ADDI:
            case SUBI:
                totalCycles = Tomasulo.ADDI_CYCLES;
                break;
            case LOAD:
                totalCycles = Tomasulo.LOAD_CYCLES;
                break;
            case STORE:
                totalCycles = Tomasulo.STORE_CYCLES;
                break;
            default:
                System.err.println("Invalid instruction type: " + instructionType);
        }

    	this.instructionStatus.setExecutionStart(currentCycle);
        this.instructionStatus.setExecutionComplete(currentCycle+totalCycles-1);
    }
    public ITypes getInstructionType() {
        return instructionType;
    }

    public void setInstructionType(ITypes instructionType) {
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

    public static Instruction parseInstruction(String line) {
        String[] parts = line.split("\\s*,\\s*|\\s*\\(\\s*|\\s*\\)\\s*|\\s+");

        if (parts.length >= 2) {
            String opcode = parts[0];
            String dest = parts[1];
            String src1 = null;
            String src2 = null;
            String immediate = null;

            if (parts.length >= 3) {
                src1 = parts[2];
            }

            if (parts.length >= 4) {
                src2 = parts[3];
            }

            if (opcode.equals("LOAD") || opcode.equals("STORE")) {
                immediate = parts[2];
                src1 = null;
            } else if (opcode.equals("ADDI") || opcode.equals("SUBI") || opcode.equals("MULTI") || opcode.equals("DIVI")) {
                immediate = parts[3];
            }

            Instruction instruction = new Instruction();
            instruction.setInstructionType(ITypes.getInstructionType(opcode));
            instruction.setRs(src1);
            instruction.setRd(dest);
            instruction.setRt(src2);
            instruction.setImmediateOffset(immediate);

            return instruction;
        } else {
            System.out.println("Invalid instruction format: " + line);
            return null;
        }
    }

    public String toString() {
        return instructionType + " (Rd:" + Rd + "), (Rs:" + Rs + "),"+ (!instructionType.toString().endsWith("i")&&!instructionType.toString().endsWith("I") ? "  (Rt:" + Rt + ")" :" imm: " + immediateOffset);
    }
}
