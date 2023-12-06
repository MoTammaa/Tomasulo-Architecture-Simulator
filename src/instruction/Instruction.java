package instruction;

import engine.Tomasulo;
import reservationStations.LoadStoreBuffer;
import reservationStations.ReservationStation;
import reservationStations.Station;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.InputMismatchException;

public class Instruction {
    private ITypes instructionType;
    private String Rs;
    private String Rd;
    private String Rt;
    private String immediateOffset;
    private ArrayList<InstructionStatus> instructionStatuses = new ArrayList<>();

    public Instruction() {
    	instructionStatuses.add(new InstructionStatus());
    }
    public Instruction(ITypes instructionType, String rs, String rd, String rt) {
        this.instructionType = instructionType;
        Rs = rs;
        Rd = rd;
        Rt = rt;
        instructionStatuses.add(new InstructionStatus());
    }
    public Instruction(ITypes instructionType, String rs, String rd, String rt, String immediateOffset) {
        this.instructionType = instructionType;
        Rs = rs;
        Rd = rd;
        Rt = rt;
        this.immediateOffset = immediateOffset;
        instructionStatuses.add(new InstructionStatus());
    }
    public void setWriteBack(int currentCycle) {
    	this.instructionStatuses.get(instructionStatuses.size()-1).setWriteBack(currentCycle);
    }


    public void setIssue(int currentCycle) {
        instructionStatuses.add(new InstructionStatus());
    	this.instructionStatuses.get(instructionStatuses.size()-1).setIssue(currentCycle);
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
            case ADDI, SUBI:
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

    	this.instructionStatuses.get(instructionStatuses.size()-1).setExecutionStart(currentCycle);
        this.instructionStatuses.get(instructionStatuses.size()-1).setExecutionComplete(currentCycle+totalCycles-1);
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
        return instructionStatuses.get(instructionStatuses.size()-1);
    }

    public void setInstructionStatus(InstructionStatus instructionStatus) {
        this.instructionStatuses.set(instructionStatuses.size()-1, instructionStatus);
    }

    public String execute(Station station){
        	String result;
        	switch (instructionType) {
            case ADD, ADDI:
                if (instructionType.toString().endsWith("I")) {
                    result = Long.toString(Long.parseLong(((ReservationStation)station).getVj()) + Long.parseLong(((ReservationStation)station).getVk()));
                } else {
                    result = Rs.startsWith("R") ? Long.toString(Long.parseLong(((ReservationStation)station).getVj()) + Long.parseLong(((ReservationStation)station).getVk())) :
                            Double.toString(Double.parseDouble(((ReservationStation)station).getVj()) + Double.parseDouble(((ReservationStation)station).getVk()));
                }
                break;
            case SUB, SUBI:
                if (instructionType.toString().endsWith("I")) {
                    result = Long.toString(Long.parseLong(((ReservationStation)station).getVj()) - Long.parseLong(((ReservationStation)station).getVk()));
                } else {
                    result = Rs.startsWith("R") ? Long.toString(Long.parseLong(((ReservationStation)station).getVj()) - Long.parseLong(((ReservationStation)station).getVk())) :
                            Double.toString(Double.parseDouble(((ReservationStation)station).getVj()) - Double.parseDouble(((ReservationStation)station).getVk()));
                }
                break;
            case MUL, MULI:
                if (instructionType.toString().endsWith("I")) {
                    result = Long.toString(Long.parseLong(((ReservationStation)station).getVj()) * Long.parseLong(((ReservationStation)station).getVk()));
                } else {
                    result = Rs.startsWith("R") ? Long.toString(Long.parseLong(((ReservationStation)station).getVj()) * Long.parseLong(((ReservationStation)station).getVk())) :
                            Double.toString(Double.parseDouble(((ReservationStation)station).getVj()) * Double.parseDouble(((ReservationStation)station).getVk()));
                }
                break;
            case DIV, DIVI:
                if (((ReservationStation)station).getVk().equals("0") || ((ReservationStation)station).getVk().equals("0.0")){
                    System.err.println("YOU CANNOT DIVIDE BY ZERO!! matet3ebneesh m3ak ya 7abibi");
                    ((ReservationStation)station).setVk("1");
                }
                if (instructionType.toString().endsWith("I")) {
                    result = Long.toString(Long.parseLong(((ReservationStation)station).getVj()) / Long.parseLong(((ReservationStation)station).getVk()));
                } else {
                    result = Rs.startsWith("R") ? Long.toString(Long.parseLong(((ReservationStation)station).getVj()) / Long.parseLong(((ReservationStation)station).getVk())) :
                            Double.toString(Double.parseDouble(((ReservationStation)station).getVj()) / Double.parseDouble(((ReservationStation)station).getVk()));
                }
                break;
            case LOAD, L_D: //M[]
                result = Tomasulo.getDataCache().M(Integer.parseInt(((LoadStoreBuffer)station).getInstruction().getImmediateOffset()));
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


    public static Instruction parseInstruction(String line, int instructionIdx){
        if (line.startsWith("//")) return null;
        boolean hasLabel = line.contains(":");
        String [] parts = line.split("\\s*:\\s*");
        String label="";
        if (hasLabel) {
            label = parts[0];
            if (parts.length > 1) line = parts[1];
            if (line.split("\\s*:\\s*").length > 1) {
                throw new InputMismatchException("Invalid label format: " + line);
            }
        }

        if (line.split("\\s*,\\s*").length < 2) {
            if (hasLabel) System.err.println("Invalid label format: " + line + " (missing instruction), so inserting HALT instruction");
            line = "HLT, exit(0)";
        }

        parts = line.split("\\s*,\\s*|\\s*\\(\\s*|\\s*\\)\\s*|\\s*//|\\s+");
        if (parts.length >= 2 ) {
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
            } else if ( type == ITypes.BNEZ) {
                src1 = parts[1];
                src2 = null;
                dest = "B";
                immediate = parts[2];


            }

            if (!label.isEmpty()) {
                // {label}: {instruction} {dest}, {src1}, {src2} ===> {label,instruction,dest src1, src2, ....}
                Tomasulo.getLabels().put(label, instructionIdx);
            }

            Instruction instruction = new Instruction();
            instruction.setInstructionType(type);
            instruction.setRs(src1);
            instruction.setRd(dest);
            instruction.setRt(src2);
            instruction.setImmediateOffset(immediate);

            return instruction;
        } else {
            System.err.println("Invalid instruction format: " + line);
            return null;
        }
    }

    public String toString() {
        StringBuilder statuses = new StringBuilder("[");
        for (int i = 1; i < instructionStatuses.size(); i++) {
            statuses.append(instructionStatuses.get(i).toString()).append((i == instructionStatuses.size()-1 ? "" : ", "));
        }
        statuses.append("]");
        return instructionType + " (Rd:" + Rd + "), (Rs:" + Rs + "),"+ (!instructionType.toString().endsWith("i")&&
                                                                        !instructionType.toString().endsWith("I") &&
                                                                        !instructionType.toString().startsWith("L") &&
                                                                        !instructionType.toString().startsWith("S") &&
                                                                        instructionType != ITypes.BNEZ?
                                                                            "  (Rt:" + Rt + ")" : " imm: " + immediateOffset) + " "
                                                                + statuses.toString();
    }

}
