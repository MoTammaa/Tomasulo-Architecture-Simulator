package engine;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import caches.*;
import instruction.*;
import registerFile.*;
import reservationStations.*;
import view.CodeIDE;
import view.Tables;
import view.TomasuloInputs;

public class Tomasulo {

    public static int LOAD_CYCLES = 2, STORE_CYCLES = 2, ADD_CYCLES = 2, SUB_CYCLES = 2
            , MUL_CYCLES = 5, DIV_CYCLES = 6, BNEZ_CYCLES = 1, ADDI_CYCLES = 1;
    public static int MAX_LOAD_BUFFERS = 2, MAX_STORE_BUFFERS = 2
            , MAX_ADD_STATIONS = 2, MAX_MUL_DIV_STATIONS = 2;
    private static final int MAX_INSTRUCTIONS = 100, MAX_MEMORY = 500;


    private static String instructionFilePath;
    private static InstructionCache Icache;
    private static DataCache Dcache;

    private static LoadStoreBuffer[] loadBuffers,     storeBuffers;
    private static ReservationStation[] addSubReservationStations,    mulDivReservationStations;

    private static RegisterFile registerFile;
    private static HashMap<String, Integer> labels;

    private static int currentCycle = 0;

    public static void main(String[] args) {
        CodeIDE.startCoding();
    }
  public static void initializeTomasulo()  {
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
        Tomasulo.loadDataFromFile(instructionFilePath + ".txt");
    }

    // getters and setters

    public void setAddCycles(int cycles) {
        ADD_CYCLES = cycles;
    }

    public void setSubCycles(int cycles) {
        SUB_CYCLES = cycles;
    }
    public void setMulCycles(int cycles) {
        MUL_CYCLES = cycles;
    }

    public void setDivCycles(int cycles) {
        DIV_CYCLES = cycles;
    }
    public void setLoadCycles(int cycles) {
        LOAD_CYCLES = cycles;
    }

    public void setStoreCycles(int cycles) {
        STORE_CYCLES = cycles;
    }

    public void setBNEZCycles(int cycles) {
        BNEZ_CYCLES = cycles;
    }

    public void setAddSubStations(int stations) {
        MAX_ADD_STATIONS = stations;
    }

    public void setMulDivStations(int stations) {
        MAX_MUL_DIV_STATIONS = stations;
    }

    public void setLoadBuffers(int buffers) {
        MAX_LOAD_BUFFERS = buffers;
    }

    public void setStoreBuffers(int buffers) {
        MAX_STORE_BUFFERS = buffers;
    }

    public void setInstructionFilePath(String filePath) {
        instructionFilePath = filePath;
    }

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
        if (Icache.getInstructions()[0] == null) {
            System.err.println("ERROR! NO INSTRUCTIONS FOUND!!");
            System.exit(1);
        }
        if (Icache.getInstructions()[Icache.getCurrentCapacity()-1].getInstructionType() != ITypes.HLT) {
            Icache.addInstruction(new Instruction(ITypes.HLT, "exit", "0", null));
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
        if (instruction.getInstructionType() == ITypes.LOAD || instruction.getInstructionType() == ITypes.L_D) {
            return getAvailableLoadBuffer();
        } else if (instruction.getInstructionType() == ITypes.STORE || instruction.getInstructionType() == ITypes.S_D) {
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
        if (instruction.getInstructionType() == ITypes.LOAD || instruction.getInstructionType() == ITypes.STORE
                || instruction.getInstructionType() == ITypes.L_D || instruction.getInstructionType() == ITypes.S_D) {
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
        List<String[]> data = new ArrayList<>();

        // Print the current cycle
        System.out.println("\n\n\n\n***********************************************************************Current Cycle: " + getCurrentCycle() +"****************************************************************************************");
        // Print the current state of the load buffers
        System.out.println("Load Buffers: ");
        for (LoadStoreBuffer loadBuffer : loadBuffers) {
            System.out.println(loadBuffer);
            data.add(loadBuffer.getTableData());
        }
        Tables.addToTablesBuffer(data.toArray(new String[0][]), Tables.TableType.LOAD_BUFFER, getCurrentCycle());
        data.clear();

        System.out.println("-----------------------");
        // Print the current state of the store buffers
        System.out.println("Store Buffers: ");
        for (LoadStoreBuffer storeBuffer : storeBuffers) {
            System.out.println(storeBuffer);
            data.add(storeBuffer.getTableData());
        }
        Tables.addToTablesBuffer(data.toArray(new String[0][]), Tables.TableType.STORE_BUFFER, getCurrentCycle());
        data.clear();

        System.out.println("-----------------------");
        // Print the current state of the add/sub reservation stations
        System.out.println("Add/Sub Reservation Stations: ");
        for (ReservationStation addSubReservationStation : addSubReservationStations) {
            System.out.println(addSubReservationStation);
            data.add(addSubReservationStation.getTableData());
        }
        Tables.addToTablesBuffer(data.toArray(new String[0][]), Tables.TableType.ADD_SUB_STATIONS, getCurrentCycle());
        data.clear();

        System.out.println("-----------------------");
        // Print the current state of the multiply/divide reservation stations
        System.out.println("Mul/Div Reservation Stations: ");
        for (ReservationStation mulDivReservationStation : mulDivReservationStations) {
            System.out.println(mulDivReservationStation);
            data.add(mulDivReservationStation.getTableData());
        }
        Tables.addToTablesBuffer(data.toArray(new String[0][]), Tables.TableType.MUL_DIV_STATIONS, getCurrentCycle());
        data.clear();

        System.out.println("-----------------------");
        // Print the current state of the register file
        System.out.println("Register File: ");
        System.out.println(registerFile);
        System.out.println("-----------------------");

        String [][][] registerFileData = registerFile.getTableData();
        Tables.addToTablesBuffer(registerFileData[0], Tables.TableType.REGISTER_FILE, getCurrentCycle());
        Tables.addToTablesBuffer(registerFileData[1], Tables.TableType.REGISTER_FILE, getCurrentCycle());

        // Print the current state of the instructions
        System.out.println("Instructions: ");
        System.out.println(Icache);
        System.out.println("-----------------------");
        Tables.addToTablesBuffer(Icache.getTableData(), Tables.TableType.INSTRUCTION_CACHE, getCurrentCycle());

        System.out.println("Data Memory (Only non-zero values are displayed):");
        System.out.println(Dcache);
        System.out.println("-----------------------");
        Tables.addToTablesBuffer(Dcache.getTableData(), Tables.TableType.DATA_CACHE, getCurrentCycle());

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
            if (currentCycle > 100) {
                System.err.println("Simulation is taking too long. Terminating...");
                break;
            }
        }

        System.out.println("===========================Total Cycles: " + (getCurrentCycle()-1));
        Tables.displayAllTables();
    }

}
