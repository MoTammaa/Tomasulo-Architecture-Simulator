package caches;

import engine.Tomasulo;
import instruction.Instruction;

public class InstructionCache extends Cache{
    private Instruction[] instructions;
    private int lastInstruction = -1, PC = 0;

    public InstructionCache(int size) {
        this.instructions = new Instruction[size];
        this.size = size;
    }


    public int getCurrentCapacity() {
        return Math.max(0,Math.min(lastInstruction,size-1));
    }

    public int getCurrentInstructionIndex() {
        return PC;
    }
    public Instruction getCurrentInstruction() {
        return instructions[PC];
    }
    public void addInstruction(Instruction instruction) {
        if (lastInstruction < size-1) {
            instructions[++lastInstruction] = instruction;
        } else {
            System.err.println("Instruction Cache is full");
        }
    }
    public boolean issueInstruction() {
        if (PC <= lastInstruction) {
            if (Tomasulo.getRegisterFile().getRegister("B").equals("1")) return false;
            if (!Tomasulo.issueInstruction(this.instructions[PC])) return false;
            PC++;
            return true;
        } else {
            return false;
        }
    }
    public void reset() {
        PC = 0;
    }
    public boolean isFinished() {
        return PC > lastInstruction;
    }

    public Instruction M(int address) {
        return instructions[address];
    }

    public Instruction M(String address) {
        return instructions[Integer.parseInt(address)];
    }

    public void setM(int address, Instruction instruction) {
        this.instructions[address] = instruction;
    }
    public void setM(String address, Instruction instruction) {
        this.instructions[Integer.parseInt(address)] = instruction;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Instruction Cache:\n");
        for (int i = 0; i <= lastInstruction; i++) {
            sb.append("@").append(i).append(": ").append(instructions[i]).append("\n");
        }

        if (lastInstruction < size-1)
            sb.append("@").append(lastInstruction+1).append(": NULL \n@.....\n").append("Rest Empty\n");

        return sb.toString();
    }
}
