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

    public static final int LOAD_CYCLES = 1;
    public static final int STORE_CYCLES = 2;
    public static final int ADD_CYCLES = 2;
    public static final int SUB_CYCLES = 2;
    public static final int MUL_CYCLES = 5;
    public static final int DIV_CYCLES = 10;
    public static final int BNEZ_CYCLES = 1;
    public static final int ADDI_CYCLES = 1;
    private static final int MAX_LOAD_BUFFERS = 10;
    private static final int MAX_STORE_BUFFERS = 10;
    private static final int MAX_ADD_STATIONS = 3;
    private static final int MAX_MUL_DIV_STATIONS = 10;
    private static final int MAX_REGISTERS = 32;
    private static final int MAX_INSTRUCTIONS = 100;
    private static InstructionCache Icache;
    
    private static LoadStoreBuffer[] loadBuffers;
    private static int totalLoadBuffers;

    private static LoadStoreBuffer[] storeBuffers;
    private static int totalStoreBuffers;

    private static ReservationStation[] addSubReservationStations;
    private static int totalAddReservationStations;

    private static ReservationStation[] mulDivReservationStations;
    private static RegisterFile registerFile;
    private static int totalRegisters;

    private static Instruction[] instructions;
    private static int totalInstructions;

    private static int totalLoadStoreCycles;
    private static int totalAddSubCycles;
    private static int totalMulCycles;
    private static int totalDivCycles;

    private static final int DEFAULT_CYCLES = 2;

    public Tomasulo() {
        this.loadBuffers = new LoadStoreBuffer[MAX_LOAD_BUFFERS];
        for (int i = 0; i < loadBuffers.length; i++) {
            loadBuffers[i]= new LoadStoreBuffer("L" + (i+1));
        }
        this.storeBuffers = new LoadStoreBuffer[MAX_STORE_BUFFERS];
        for(int i = 0; i < storeBuffers.length; i++) {
            storeBuffers[i]= new LoadStoreBuffer("S" + (i+1));
        }
        this.addSubReservationStations = new ReservationStation[MAX_ADD_STATIONS];
        for(int i = 0; i < addSubReservationStations.length; i++) {
            addSubReservationStations[i]= new ReservationStation("A" + (i+1));
        }
        this.mulDivReservationStations = new ReservationStation[MAX_MUL_DIV_STATIONS];
        for(int i = 0; i < mulDivReservationStations.length; i++) {
            mulDivReservationStations[i]= new ReservationStation("M" + (i+1));
        }
        this.registerFile = new RegisterFile(); 
        this.instructions = new Instruction[MAX_INSTRUCTIONS];
        this.totalLoadStoreCycles = DEFAULT_CYCLES;
        this.totalAddSubCycles = DEFAULT_CYCLES;
        this.totalMulCycles = DEFAULT_CYCLES;
        this.totalDivCycles = DEFAULT_CYCLES;
        this.Icache = new InstructionCache(10);
    }
        
        public static void loadDataFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int instructionIndex = 0;
            while ((line = reader.readLine()) != null) {
                Instruction instruction = Instruction.parseInstruction(line);
                if (instruction != null) {
                    instructions[instructionIndex] = instruction;
                    instructionIndex++;
                    Icache.addInstruction(instruction);
                }
            }
            System.out.println(".......\n"+Arrays.toString(addSubReservationStations));
            totalInstructions = instructionIndex;
        } catch (IOException e) {
            System.err.println("Error reading from file: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private static ReservationStation getFirstAvailableReservationStation(ReservationStation[] stations) {
        for (ReservationStation station : stations) {
            if (!station.isOccupied()) {
                return station;
            }
        }
        return null;
    }
    private static LoadStoreBuffer getFirstAvailableLoadBuffer() {
        for (LoadStoreBuffer loadBuffer : loadBuffers) {
            if (!loadBuffer.isOccupied()) {
                return loadBuffer;
            }
        }
        return null;
    }
    private static LoadStoreBuffer getFirstAvailableStoreBuffer() {
        for (LoadStoreBuffer storeBuffer : storeBuffers) {
            if (!storeBuffer.isOccupied()) {
                return storeBuffer;
            }
        }
        return null;
    }
    private static LoadStoreBuffer getAvailableLoadBuffer() {
        return getFirstAvailableLoadBuffer();
    }
    private static LoadStoreBuffer getAvailableStoreBuffer() {
        return getFirstAvailableStoreBuffer();
    }
    private static LoadStoreBuffer getAvailableLoadStoreBuffer(Instruction instruction) {
        if (instruction.getInstructionType().equalsIgnoreCase("Load")) {
            return getAvailableLoadBuffer();
        } else if (instruction.getInstructionType().equalsIgnoreCase("Store")) {
            return getAvailableStoreBuffer();
        }

        System.out.println("Unsupported instruction type: " + instruction.getInstructionType());
        return null;
    }
    private static ReservationStation getAvailableAddSubReservationStation() {
        return getFirstAvailableReservationStation(addSubReservationStations);
    }
    private static ReservationStation getAvailableMulDivReservationStation() {
        return getFirstAvailableReservationStation(mulDivReservationStations);
    }
    public static boolean issueInstruction(Instruction instruction) {
        // Determine the type of instruction and handle accordingly
        if (instruction.getInstructionType().equalsIgnoreCase("Load") || instruction.getInstructionType().equalsIgnoreCase("Store")) {
            return issueLoadStoreInstruction(instruction);
        } else if (instruction.getInstructionType().equalsIgnoreCase("Add") || instruction.getInstructionType().equalsIgnoreCase("Sub")|| instruction.getInstructionType().equalsIgnoreCase("SUBI")|| instruction.getInstructionType().equalsIgnoreCase("ADDI")||instruction.getInstructionType().equalsIgnoreCase("bnez")) {
            return issueAddSubInstruction(instruction);
        } else if (instruction.getInstructionType().equalsIgnoreCase("Mult") || instruction.getInstructionType().equalsIgnoreCase("Div")) {
            return issueMulDivInstruction(instruction);
        } else {
            System.out.println("Unsupported instruction type: " + instruction.getInstructionType());
            return false;
        }
    }
    private static boolean issueLoadStoreInstruction(Instruction instruction) {
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
            return false;
        }
        return true;
    }
    private static boolean issueAddSubInstruction(Instruction instruction) {
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
            return false;
        }
        return true;
    }
	private static boolean issueMulDivInstruction(Instruction instruction) {
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
            return false;
        }
        return true;
    }
    public static void executeInstructions() {
        // Implementation for executing instructions
        startExecutionInStation(addSubReservationStations);

        startExecutionInStation(mulDivReservationStations);

        startExecutionInBuffer(loadBuffers);

        startExecutionInBuffer(storeBuffers);


    }

    private static void startExecutionInStation(ReservationStation[] addSubReservationStations) {
        for (ReservationStation station : addSubReservationStations) {
            if (station.isOccupied()) {
                Instruction instruction = station.getInstruction();
                if (instruction.getInstructionStatus().getIssue() != null && instruction.getInstructionStatus().getExecutionStart() == null) {
                    // check if the operands are ready
                    if (station.isReady()) {
                        instruction.startExecution(getCurrentCycle());
                        System.out.println("Instruction " + instruction.toString() + " started execution at cycle " + getCurrentCycle());
                    }
//                  else System.out.println("Instruction " + instruction.toString() + " is not ready to start execution at cycle " + getCurrentCycle());
                }
            }
        }
    }

    private static void startExecutionInBuffer(LoadStoreBuffer[] buffers) {
        for (LoadStoreBuffer lsBuffer : buffers) {
            if (lsBuffer.isOccupied()) {
                Instruction instruction = lsBuffer.getInstruction();
                if (instruction.getInstructionStatus().getIssue() != null && instruction.getInstructionStatus().getExecutionStart() == null) {
                    instruction.startExecution(getCurrentCycle());
                    System.out.println("Instruction " + instruction.toString() + " started execution at cycle " + getCurrentCycle());
                }
            }
        }
    }

    public static void writeBack() {
        // Implementation for writing back results
    }

    public static void broadcastResult() {
        // Implementation for broadcasting results
    }

    public static void print() {
        // Implementation for printing
    }
    
    public static void printInstructions() {
            System.out.println(Icache);
    }
    
    private static int getCurrentCycle() {
        return totalLoadStoreCycles + totalAddSubCycles + totalMulCycles + totalDivCycles;
    }

    private static void incrementTotalCycles(Instruction instruction) {
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
    
    public static void simulate() {
        // Implementation for simulation
        while (!Icache.isFinished()) {
            if(!Icache.issueInstruction()) System.out.println("-----Cannot Issue the current "+
                                                Icache.getCurrentInstruction().getInstructionType()+"Instruction-----");
        }
        // print reservation stations
        System.out.println("Reservation Stations:");
        System.out.println("Add/Sub:");
        for (ReservationStation station : addSubReservationStations) {
            System.out.println(station);
        }
    }

    public static void main(String[] args) {
        System.out.println("Hello World!");
        Tomasulo tomasulo = new Tomasulo();
        Tomasulo.loadDataFromFile("ins1.txt");
        Tomasulo.simulate();
        Tomasulo.printInstructions();
    }

}
