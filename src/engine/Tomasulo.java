package engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import caches.*;
import instruction.*;
import registerFile.*;
import reservationStations.*;

public class Tomasulo {
    private static final int MAX_LOAD_BUFFERS = 10;
    private static final int MAX_STORE_BUFFERS = 10;
    private static final int MAX_ADD_STATIONS = 2;
    private static final int MAX_MUL_DIV_STATIONS = 10;
    private static final int MAX_REGISTERS = 32;
    private static final int MAX_INSTRUCTIONS = 100;
    private InstructionCache Icache;
    
    private LoadStoreBuffer[] loadBuffers;
    private int totalLoadBuffers;

    private LoadStoreBuffer[] storeBuffers;
    private int totalStoreBuffers;

    private ReservationStation[] addSubReservationStations;
    private int totalAddReservationStations;

    private ReservationStation[] mulDivReservationStations;
    private RegisterFile registerFile;
    private int totalRegisters;

    private Instruction[] instructions;
    private int totalInstructions;

    private int totalLoadStoreCycles;
    private int totalAddSubCycles;
    private int totalMulCycles;
    private int totalDivCycles;

    private static final int DEFAULT_CYCLES = 2;

    public Tomasulo() {
        this.loadBuffers = new LoadStoreBuffer[MAX_LOAD_BUFFERS];  
        for (int i = 0; i < loadBuffers.length; i++) {
            loadBuffers[i]= new LoadStoreBuffer();
        }
        this.storeBuffers = new LoadStoreBuffer[MAX_STORE_BUFFERS];
        for(int i = 0; i < storeBuffers.length; i++) {
            storeBuffers[i]= new LoadStoreBuffer();
        }
        this.addSubReservationStations = new ReservationStation[MAX_ADD_STATIONS];
        for(int i = 0; i < addSubReservationStations.length; i++) {
            addSubReservationStations[i]= new ReservationStation();
        }
        this.mulDivReservationStations = new ReservationStation[MAX_MUL_DIV_STATIONS];
        for(int i = 0; i < mulDivReservationStations.length; i++) {
            mulDivReservationStations[i]= new ReservationStation();
        }
        this.registerFile = new RegisterFile(); 
        this.instructions = new Instruction[MAX_INSTRUCTIONS];
        

        this.totalLoadStoreCycles = DEFAULT_CYCLES;
        this.totalAddSubCycles = DEFAULT_CYCLES;
        this.totalMulCycles = DEFAULT_CYCLES;
        this.totalDivCycles = DEFAULT_CYCLES;
        this.Icache = new InstructionCache(10);
    }
    
        public void loadDataFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int instructionIndex = 0;

            while ((line = reader.readLine()) != null) {
                Instruction instruction = parseInstruction(line);
                if (instruction != null) {
                    instructions[instructionIndex] = instruction;
                    instructionIndex++;
                    Icache.addInstruction(instruction);
                    issueInstruction(instruction);
                }
            }
            System.out.println(".......\n"+Arrays.toString(addSubReservationStations));
            totalInstructions = instructionIndex;
        } catch (IOException e) {
            System.err.println("Error reading from file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private ReservationStation getFirstAvailableReservationStation(ReservationStation[] stations) {
        for (ReservationStation station : stations) {
            if (!station.isOccupied()) {
                return station;
            }
        }
        return null;
    }

    private LoadStoreBuffer getFirstAvailableLoadBuffer() {
        for (LoadStoreBuffer loadBuffer : loadBuffers) {
            if (!loadBuffer.isOccupied()) {
                return loadBuffer;
            }
        }
        return null;
    }

    private LoadStoreBuffer getFirstAvailableStoreBuffer() {
        for (LoadStoreBuffer storeBuffer : storeBuffers) {
            if (!storeBuffer.isOccupied()) {
                return storeBuffer;
            }
        }
        return null;
    }

    private LoadStoreBuffer getAvailableLoadBuffer() {
        return getFirstAvailableLoadBuffer();
    }

    private LoadStoreBuffer getAvailableStoreBuffer() {
        return getFirstAvailableStoreBuffer();
    }

    private LoadStoreBuffer getAvailableLoadStoreBuffer(Instruction instruction) {
        if (instruction.getInstructionType().equalsIgnoreCase("Load")) {
            return getAvailableLoadBuffer();
        } else if (instruction.getInstructionType().equalsIgnoreCase("Store")) {
            return getAvailableStoreBuffer();
        }

        System.out.println("Unsupported instruction type: " + instruction.getInstructionType());
        return null;
    }

    private ReservationStation getAvailableAddSubReservationStation() {
        return getFirstAvailableReservationStation(addSubReservationStations);
    }

    private ReservationStation getAvailableMulDivReservationStation() {
        return getFirstAvailableReservationStation(mulDivReservationStations);
    }
    
    public void issueInstruction(Instruction instruction) {
        // Determine the type of instruction and handle accordingly
        if (instruction.getInstructionType().equalsIgnoreCase("Load") || instruction.getInstructionType().equalsIgnoreCase("Store")) {
            issueLoadStoreInstruction(instruction);
        } else if (instruction.getInstructionType().equalsIgnoreCase("Add") || instruction.getInstructionType().equalsIgnoreCase("Sub")|| instruction.getInstructionType().equalsIgnoreCase("SUBI")|| instruction.getInstructionType().equalsIgnoreCase("ADDI")) {
            issueAddSubInstruction(instruction);
        } else if (instruction.getInstructionType().equalsIgnoreCase("Mult") || instruction.getInstructionType().equalsIgnoreCase("Div")) {
            issueMulDivInstruction(instruction);
        } else {
            System.out.println("Unsupported instruction type: " + instruction.getInstructionType());
        }
    }

    private void issueLoadStoreInstruction(Instruction instruction) {
        // Get the available load or store buffer
        LoadStoreBuffer loadStoreBuffer = getAvailableLoadStoreBuffer(instruction);

        if (loadStoreBuffer != null && !loadStoreBuffer.isOccupied()) {
            // Set the issue cycle in the instruction status
            instruction.getInstructionStatus().setIssue(getCurrentCycle());

            // Issue the instruction to the load or store buffer
            loadStoreBuffer.issueInstruction(instruction);
            

            
            incrementTotalCycles(instruction);

            System.out.println("Load/Store Instruction issued: " + instruction.toString());
        } else {
            System.out.println("Load/Store buffer is occupied. Cannot issue instruction.");
        }
    }

    private void issueAddSubInstruction(Instruction instruction) {
        // Get the available add/sub reservation station
        ReservationStation addSubReservationStation = getAvailableAddSubReservationStation();

        if (addSubReservationStation != null) {
            instruction.getInstructionStatus().setIssue(getCurrentCycle());

            addSubReservationStation.issueInstruction(instruction);
            
            
            System.out.println(addSubReservationStation);
            


            incrementTotalCycles(instruction);

            System.out.println("Add/Sub Instruction issued: " + instruction.toString());
        } else {
            System.out.println("Add/Sub reservation station is occupied. Cannot issue instruction.");
        }
    }

	private void issueMulDivInstruction(Instruction instruction) {
        // Get the available multiply/divide reservation station
        ReservationStation mulDivReservationStation = getAvailableMulDivReservationStation();

        if (mulDivReservationStation != null && !mulDivReservationStation.isOccupied()) {
            // Set the issue cycle in the instruction status
            instruction.getInstructionStatus().setIssue(getCurrentCycle());

            // Issue the instruction to the multiply/divide reservation station
            mulDivReservationStation.issueInstruction(instruction);

            // Other logic for handling the issue process

            // Increment the appropriate total cycle count based on the type of instruction
            incrementTotalCycles(instruction);

            System.out.println("Mul/Div Instruction issued: " + instruction.toString());
        } else {
            System.out.println("Mul/Div reservation station is occupied. Cannot issue instruction.");
        }
    }

    // Rest of the methods remain the same


    public void executeInstructions() {
        // Implementation for executing instructions
    }

    public void writeBack() {
        // Implementation for writing back results
    }

    public void broadcastResult() {
        // Implementation for broadcasting results
    }

    public void print() {
        // Implementation for printing
    }
    
    public void printInstructions() {
            System.out.println(Icache);
    }
    
    private int getCurrentCycle() {
        return totalLoadStoreCycles + totalAddSubCycles + totalMulCycles + totalDivCycles;
    }

    private void incrementTotalCycles(Instruction instruction) {
        // Increment the appropriate total cycle count based on the type of instruction
        switch (instruction.getInstructionType()) {
            case "Load":
            case "Store":
                totalLoadStoreCycles++;
                break;
            case "Add":
            case "Sub":
                totalAddSubCycles++;
                break;
            case "Mul":
            case "Div":
                totalMulCycles++;
                break;
        }
    }
    
    public void simulate() {
        // Implementation for simulation
    }

    public static void main(String[] args) {
        System.out.println("Hello World!");
        Tomasulo tomasulo = new Tomasulo();
        tomasulo.loadDataFromFile("ins1.txt");
        tomasulo.printInstructions();
    }
    private Instruction parseInstruction(String line) {
        String[] parts = line.split("\\s*,\\s*|\\s*\\(\\s*|\\s*\\)\\s*|\\s{1,}");

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
            instruction.setInstructionType(opcode);
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



}
