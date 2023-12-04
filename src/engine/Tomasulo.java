package engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.PriorityQueue;

import caches.*;
import instruction.*;
import registerFile.*;
import reservationStations.*;

public class Tomasulo {

    public static final int LOAD_CYCLES = 2, STORE_CYCLES = 2, ADD_CYCLES = 2, SUB_CYCLES = 2
                            , MUL_CYCLES = 5, DIV_CYCLES = 6, BNEZ_CYCLES = 1, ADDI_CYCLES = 1;
    private static final int MAX_LOAD_BUFFERS = 2, MAX_STORE_BUFFERS = 2
                        , MAX_ADD_STATIONS = 2, MAX_MUL_DIV_STATIONS = 2;
    private static final int MAX_INSTRUCTIONS = 100,         MAX_MEMORY = 500;

    private static final InstructionCache Icache;
    private static final DataCache Dcache;

    private static final LoadStoreBuffer[] loadBuffers,     storeBuffers;
    private static final ReservationStation[] addSubReservationStations,    mulDivReservationStations;

    private static final RegisterFile registerFile;
    private static final HashMap<String, Integer> labels;

    private static int currentCycle = 0;

  static  {
        loadBuffers = new LoadStoreBuffer[MAX_LOAD_BUFFERS];
        for (int i = 0; i < loadBuffers.length; i++) {
            loadBuffers[i]= new LoadStoreBuffer("L" + (i+1));
        }

        storeBuffers = new LoadStoreBuffer[MAX_STORE_BUFFERS];
        for(int i = 0; i < storeBuffers.length; i++) {
            storeBuffers[i]= new LoadStoreBuffer("S" + (i+1));
        }

        addSubReservationStations = new ReservationStation[MAX_ADD_STATIONS];
        for(int i = 0; i < addSubReservationStations.length; i++) {
            addSubReservationStations[i]= new ReservationStation("A" + (i+1));
        }

        mulDivReservationStations = new ReservationStation[MAX_MUL_DIV_STATIONS];
        for(int i = 0; i < mulDivReservationStations.length; i++) {
            mulDivReservationStations[i]= new ReservationStation("M" + (i+1));
        }

        registerFile = new RegisterFile();
        Icache = new InstructionCache(MAX_INSTRUCTIONS);
        Dcache = new DataCache(MAX_MEMORY);
        labels = new HashMap<>();
        Tomasulo.loadDataFromFile("ins1.txt");
    }

    // getters and setters
    public static LoadStoreBuffer[] getStoreBuffers() {
        return storeBuffers;
    }
    public static ReservationStation[] getAddSubReservationStations() {
        return addSubReservationStations;
    }
    public static ReservationStation[] getMulDivReservationStations() {
        return mulDivReservationStations;
    }
    public static RegisterFile getRegisterFile() {
        return registerFile;
    }
    public static DataCache getDataCache() {
        return Dcache;
    }
    public static InstructionCache getInstructionCache() {
        return Icache;
    }
    public static HashMap<String,Integer> getLabels() {
        return labels;
    }



    public static void loadDataFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) continue;
                Instruction instruction = Instruction.parseInstruction(line, Icache.getCurrentCapacity());
                if (instruction != null) {
                    Icache.addInstruction(instruction);
                }
            }
//            System.out.println(".......\n"+Arrays.toString(addSubReservationStations));
        } catch (IOException e) {
            System.err.println("Error reading from file: " + e.getMessage());
        }
        // now loop on the BNEZ instructions to replace the labels by the hashmap values
        for (Instruction inst: Icache.getInstructions()) {
            if (inst == null) break;
            if ( inst.getInstructionType() == ITypes.BNEZ) {
                if(!labels.containsKey(inst.getImmediateOffset())) {
                    System.err.println("WARNING! CANNOT FIND LABEL: '''''" + inst.getImmediateOffset() + "''''' in instruction: " + inst
                            + "!! The code will initialize loop to first instruction and continue!!");
                }
                inst.setImmediateOffset(Integer.toString(labels.getOrDefault(inst.getImmediateOffset(), 0)));
            }
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
        if (instruction.getInstructionType() == ITypes.LOAD ) {
            return getAvailableLoadBuffer();
        } else if (instruction.getInstructionType() == ITypes.STORE) {
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
        if (instruction.getInstructionType() == ITypes.LOAD || instruction.getInstructionType() == ITypes.STORE) {
            return issueLoadStoreInstruction(instruction);
        } else if (instruction.getInstructionType() == ITypes.ADD ||
                    instruction.getInstructionType() == ITypes.SUB ||
                    instruction.getInstructionType() == ITypes.SUBI ||
                    instruction.getInstructionType() == ITypes.ADDI ||
                    instruction.getInstructionType() == ITypes.BNEZ) {
            return issueAddSubInstruction(instruction);
        } else if (instruction.getInstructionType() == ITypes.MUL ||
                instruction.getInstructionType() == ITypes.DIV) {
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
            instruction.setIssue(getCurrentCycle());

            // Issue the instruction to the load or store buffer
            loadStoreBuffer.issueInstruction(instruction);
            //System.out.println("Load/Store Instruction issued: " + instruction.toString());
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
            instruction.setIssue(getCurrentCycle());
            addSubReservationStation.issueInstruction(instruction);
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
            instruction.setIssue(getCurrentCycle());
            // Issue the instruction to the multiply/divide reservation station
            mulDivReservationStation.issueInstruction(instruction);
            // Other logic for handling the issue process
//            System.out.println("Mul/Div Instruction issued: " + instruction.toString());
        } else {
            System.out.println("Mul/Div reservation station is occupied. Cannot issue instruction.");
            return false;
        }
        return true;
    }
    public static void executeInstructions() {
        startExecutionInStation(addSubReservationStations);

        startExecutionInStation(mulDivReservationStations);

        startExecutionInBuffer(loadBuffers);

        startExecutionInBuffer(storeBuffers);


    }

    private static void startExecutionInStation(Station[] Stations) {
        for (Station station : Stations) {
            if (station.isOccupied()) {
                Instruction instruction = station.getInstruction();
                if (instruction.getInstructionStatus().getIssue() != null && instruction.getInstructionStatus().getExecutionStart() == null ) {
                    // check if the operands are ready
                    if (station.isReady() && instruction.getInstructionStatus().getIssue() != null && instruction.getInstructionStatus().getExecutionStart() == null
                            && instruction.getInstructionStatus().getIssue() <= getCurrentCycle() - 1) {
                        instruction.startExecution(getCurrentCycle());
                        System.out.println("|||| Instruction " + instruction + " started execution at cycle " + getCurrentCycle());
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
                if (lsBuffer.isReady() && instruction.getInstructionStatus().getIssue() != null && instruction.getInstructionStatus().getExecutionStart() == null
                        && instruction.getInstructionStatus().getIssue() <= getCurrentCycle() - 1)  {
                    instruction.startExecution(getCurrentCycle());
                    System.out.println("|||| Instruction " + instruction + " started execution at cycle " + getCurrentCycle());
                }
            }
        }
    }


    public static void writeBack() {
        PriorityQueue<Station> WBWaitingList = new PriorityQueue<>(Icache.getCurrentCapacity(), (s1, s2) -> {
            if (s1.getInstruction().getInstructionStatus().getIssue() == null) return -1;
            if (s2.getInstruction().getInstructionStatus().getIssue() == null) return 1;
            return s1.getInstruction().getInstructionStatus().getIssue() - s2.getInstruction().getInstructionStatus().getIssue();
        }); // sort the instructions according to their issue cycle

        // When an instruction finishes execution, in the next cycle, write back the result to the register file
        startWriteBackInStations(addSubReservationStations, WBWaitingList);

        startWriteBackInStations(mulDivReservationStations, WBWaitingList);

        startWriteBackInStations(loadBuffers, WBWaitingList);

        startWriteBackInStations(storeBuffers, WBWaitingList);

        // now choose the oldest highest priority instruction in the bus waiting list and write back its result
        if (!WBWaitingList.isEmpty()) {
            Station station = WBWaitingList.poll();
            station.writeBack();
            System.out.println("|| Instruction " + station.getInstruction() + " wrote back result at cycle " + getCurrentCycle());
            station.release();
        }
    }

    private static void startWriteBackInStations(Station[] addSubReservationStations, PriorityQueue<Station> BusWaitingList) {
        for (Station station : addSubReservationStations) {
            if (station.isOccupied()) {
                Instruction instruction = station.getInstruction();
                if (instruction.getInstructionStatus().getExecutionComplete() != null && instruction.getInstructionStatus().getWriteBack() == null
                        && instruction.getInstructionStatus().getExecutionComplete() <= getCurrentCycle()-1) {
                    // Write back the result to the register file or data memory, or add to the bus waiting list
                    if (instruction.getInstructionType() == ITypes.STORE || instruction.getInstructionType() == ITypes.S_D) {
                        station.writeBack();
                        System.out.println("|| Instruction " + instruction + " wrote back result at cycle " + getCurrentCycle());
                        station.release();
                    } else {
                        BusWaitingList.add(station);
                    }
                }
            }
        }
    }

    public static void broadcastResult() {
        PriorityQueue<Station> BusWaitingList = new PriorityQueue<>(Icache.getCurrentCapacity()+1, (s1, s2) -> {
            if (s1.getInstruction().getInstructionStatus().getIssue() == null) return -1;
            if (s2.getInstruction().getInstructionStatus().getIssue() == null) return 1;
            return s1.getInstruction().getInstructionStatus().getIssue() - s2.getInstruction().getInstructionStatus().getIssue();
        }); // sort the instructions according to their issue cycle


        // When an instruction finishes execution, in the next cycle, broadcast the result to all reservation stations and load buffers
        startBroadcastingInStations(addSubReservationStations, BusWaitingList);

        startBroadcastingInStations(mulDivReservationStations, BusWaitingList);

        startBroadcastingInStations(loadBuffers, BusWaitingList);

        // now choose the oldest highest priority instruction in the bus waiting list and broadcast its result
        if (!BusWaitingList.isEmpty()) {
            Station station = BusWaitingList.poll();
            station.broadcastResult();
            System.out.println("|| Instruction " + station.getInstruction() + " broadcast result at cycle " + getCurrentCycle());
        }
    }

    private static void startBroadcastingInStations(Station[] addSubReservationStations, PriorityQueue<Station> BusWaitingList) {
        for (Station station : addSubReservationStations) {
            if (station.isOccupied()) {
                Instruction instruction = station.getInstruction();
                if (instruction.getInstructionStatus().getExecutionComplete() != null &&
                        instruction.getInstructionStatus().getExecutionComplete() <= getCurrentCycle()-1) {
                    // Broadcast the result to all reservation stations and store buffers
                    if (instruction.getInstructionType() != ITypes.STORE && instruction.getInstructionType() != ITypes.S_D) {
                        BusWaitingList.add(station);
                    }
                }
            }
        }
    }

    private static boolean isAllStationsEmpty() {
        for (ReservationStation station : addSubReservationStations) {
            if (station.isOccupied()) {
                return false;
            }
        }
        for (ReservationStation station : mulDivReservationStations) {
            if (station.isOccupied()) {
                return false;
            }
        }
        for (LoadStoreBuffer loadBuffer : loadBuffers) {
            if (loadBuffer.isOccupied()) {
                return false;
            }
        }
        for (LoadStoreBuffer storeBuffer : storeBuffers) {
            if (storeBuffer.isOccupied()) {
                return false;
            }
        }
        return true;
    }

    public static void printStatus() {
        // Print the current cycle
        System.out.println("\n\n\n\n***********************************************************************Current Cycle: " + getCurrentCycle() +"****************************************************************************************");
        // Print the current state of the load buffers
        System.out.println("Load Buffers: ");
        for (LoadStoreBuffer loadBuffer : loadBuffers) {
            System.out.println(loadBuffer);
        }
        System.out.println("-----------------------");
        // Print the current state of the store buffers
        System.out.println("Store Buffers: ");
        for (LoadStoreBuffer storeBuffer : storeBuffers) {
            System.out.println(storeBuffer);
        }
        System.out.println("-----------------------");
        // Print the current state of the add/sub reservation stations
        System.out.println("Add/Sub Reservation Stations: ");
        for (ReservationStation addSubReservationStation : addSubReservationStations) {
            System.out.println(addSubReservationStation);
        }
        System.out.println("-----------------------");
        // Print the current state of the multiply/divide reservation stations
        System.out.println("Mul/Div Reservation Stations: ");
        for (ReservationStation mulDivReservationStation : mulDivReservationStations) {
            System.out.println(mulDivReservationStation);
        }
        System.out.println("-----------------------");
        // Print the current state of the register file
        System.out.println("Register File: ");
        System.out.println(registerFile);
        System.out.println("-----------------------");
        // Print the current state of the instructions
        System.out.println("Instructions: ");
        System.out.println(Icache);
        System.out.println("-----------------------");

        System.out.println("**********************************************************************************************************************************************************\n\n\n\n");

    }

    public static int getCurrentCycle(){
        return currentCycle;
    }

    
    public static void simulate() {
        while (!Icache.isFinished() || !isAllStationsEmpty()) {
            System.out.println("\n--**--**--**--**--**--**--**--**--**--**--**--Your Events Summary for Today, umm- I mean for this cycle '"+ currentCycle+"' :)--**--**--**--**--**--**--**--**--**--**--**--**--**--**--**--\n");

            if(!Icache.isFinished() && !Icache.issueInstruction()) System.out.println("----------------Cannot Issue the current "+
                                                Icache.getCurrentInstruction()+"  Instruction------------------------------\n");
            executeInstructions();
            broadcastResult();
            writeBack();
            System.out.println("\n--**--**--**--**--**--**--**--**--**--**--**--**--**--**--**--End of your Events Summary--**--**--**--**--**--**--**--**--**--**--**--**--**--**--**--");


            printStatus();

            currentCycle++;
            if (currentCycle > 50) {
                System.err.println("Simulation is taking too long. Terminating...");
                break;
            }
        }

        System.out.println("===========================Total Cycles: " + (getCurrentCycle()-1));
    }

    public static void main(String[] args) {
        System.out.println("Hello World!");
//ADD F1, F1, F3    = 1.1 + 2.4 = 3.5
//LOAD F4, 100      = 3.5
//ADD F1, F1, F4    = 3.5 + 3.5 = 7
//STORE F1, 100
        registerFile.setF(1,1.1);
        registerFile.setF(3,2.4);
        registerFile.setR(12, -2);
        Dcache.setM(100, "3.1");

        Tomasulo.simulate();
//        System.out.println(Dcache);

//        Tomasulo.printInstructions();//        Tomasulo.printStatus();
    }

}
