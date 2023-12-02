package instruction;

import engine.Tomasulo;
import reservationStations.ReservationStation;
import reservationStations.Station;

import java.util.InputMismatchException;

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
            case LOAD, L_D:
                totalCycles = Tomasulo.LOAD_CYCLES;
                break;
            case STORE, S_D:
                totalCycles = Tomasulo.STORE_CYCLES;
                break;
            case BNEZ:
                totalCycles = Tomasulo.BNEZ_CYCLES;
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

    public String execute(Station station){
        	String result;
        	switch (instructionType) {
            case ADD, ADDI:
                result = ( Rs.startsWith("R") ? Long.parseLong(((ReservationStation)station).getVj()) : Double.parseDouble(((ReservationStation)station).getVj()))
                            + ( Rt.startsWith("R") || instructionType.toString().endsWith("I") ? Long.parseLong(((ReservationStation)station).getVk()) : Double.parseDouble(((ReservationStation)station).getVk())) + "";
                break;
            case SUB, SUBI:
                result = ( Rs.startsWith("R") ? Long.parseLong(((ReservationStation)station).getVj()) : Double.parseDouble(((ReservationStation)station).getVj()))
                            - ( Rt.startsWith("R") || instructionType.toString().endsWith("I") ? Long.parseLong(((ReservationStation)station).getVk()) : Double.parseDouble(((ReservationStation)station).getVk())) + "";
                break;
            case MUL, MULI:
                result = ( Rs.startsWith("R") ? Long.parseLong(((ReservationStation)station).getVj()) : Double.parseDouble(((ReservationStation)station).getVj()))
                            * ( Rt.startsWith("R") || instructionType.toString().endsWith("I") ? Long.parseLong(((ReservationStation)station).getVk()) : Double.parseDouble(((ReservationStation)station).getVk())) + "";
                break;
            case DIV, DIVI:
                result = ( Rs.startsWith("R") ? Long.parseLong(((ReservationStation)station).getVj()) : Double.parseDouble(((ReservationStation)station).getVj()))
                            / ( Rt.startsWith("R") || instructionType.toString().endsWith("I") ? Long.parseLong(((ReservationStation)station).getVk()) : Double.parseDouble(((ReservationStation)station).getVk())) + "";
                break;
            case LOAD, L_D:
                result = Rs;
                break;
            case STORE, S_D:
                result = null;
                break;
            case BNEZ:
                result = ( ((ReservationStation)station).getVj().equals("0") || ((ReservationStation)station).getVj().equals("0.0") ? "0" : "1" );
                break;
            default:
                result = null;
                System.err.println("Invalid instruction type: " + instructionType);
        }
        	return result;
    }


    public static Instruction parseInstruction(String line) {
        String[] parts = line.split("\\s*,\\s*|\\s*\\(\\s*|\\s*\\)\\s*|\\s+");

        if (parts.length >= 2) {
            String opcode = parts[0];
            ITypes type = ITypes.getInstructionType(opcode);
            if (type == null) {
                throw new InputMismatchException("Invalid instruction type: '" + opcode+ "'");
            }
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

            if (type == ITypes.LOAD || type == ITypes.STORE || type == ITypes.L_D || type == ITypes.S_D) {
                immediate = parts[2];
                src1 = null;
            } else if (type == ITypes.ADDI || type == ITypes.SUBI || type == ITypes.MULI || type == ITypes.DIVI) {
                immediate = parts[3];
            }

            Instruction instruction = new Instruction();
            instruction.setInstructionType(type);
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
        return instructionType + " (Rd:" + Rd + "), (Rs:" + Rs + "),"+ (!instructionType.toString().endsWith("i")&&
                                                                        !instructionType.toString().endsWith("I") &&
                                                                        !instructionType.toString().startsWith("L") &&
                                                                        !instructionType.toString().startsWith("S")?
                                                                            "  (Rt:" + Rt + ")" : " imm: " + immediateOffset);
    }

}
