package reservationStations;

import engine.Tomasulo;
import instruction.ITypes;
import instruction.Instruction;
import registerFile.Register;

public class LoadStoreBuffer extends Station{
    private final String bufferName;
    private String address;
    private String fu;
    private String Q;

    public LoadStoreBuffer(String bufferName) {
        this.bufferName = this.name = bufferName;
        if (bufferName.startsWith("L")) {
            this.Q = "0";
        }
    }

    public String getName() {
        return bufferName;
    }
    public void issueInstruction(Instruction instruction) {
        this.instruction = instruction;
        this.isOccupied=true;

        if (bufferName.startsWith("L")) { // Load: read from memory
            this.Q = "0";
            this.address = instruction.getImmediateOffset(); // assuming    L.D R1, 100
            Tomasulo.getRegisterFile().setRegisterStatus(instruction.getRd(), bufferName);
//            this.fu = instruction.getRd();
        } else { // Store: read from register file
            this.Q = Tomasulo.getRegisterFile().getQ(instruction.getRd());
            if (this.Q.equals("0"))
                this.fu = Tomasulo.getRegisterFile().getRegister(instruction.getRd());
            this.address = instruction.getImmediateOffset();
        }
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public boolean isReady() {
        return isOccupied && (fu != null || instruction.getInstructionType() == ITypes.LOAD || instruction.getInstructionType() == ITypes.L_D) && address != null && Q.equals("0");
    }

    public void occupy() {
        isOccupied = true;
    }

    public void release() {
        isOccupied = false;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setFu(String fu) {
        this.fu = fu;
    }

    public String getAddress() {
        return address;
    }

    public String getFu() {
        return fu;
    }
    public void setQ(String Q) {
        this.Q = Q;
    }
    public String getQ() {
        return Q;
    }

    @Override
    public String toString() {
        return  bufferName + "{" +
                "Busy=" + (isOccupied?1:0) +
                ", address='" + address + '\'' +
                ", "  + (bufferName.startsWith("S") ? "Src" : "Dest") + "='" + (instruction == null ? "null" : instruction.getRd()) + '\'' +
                (bufferName.startsWith("L") ? "" : ", V='" + fu + '\'') +
                ", Q='" + Q + '\'' +
                ", instruction=" + instruction +
                '}';
    }

    public void writeBack() {
        instruction.setWriteBack(Tomasulo.getCurrentCycle());
        if (bufferName.startsWith("L")) { // Load: write to register file
            if (Tomasulo.getRegisterFile().getRegisterStatus(instruction.getRd()).equals(bufferName)){
                Tomasulo.getRegisterFile().setRegisterValue(instruction.getRd(), instruction.execute(this));
                Tomasulo.getRegisterFile().setRegisterStatus(instruction.getRd(), "0");
            }
        } else { // Store: write to memory
            Tomasulo.getDataCache().setM(address, fu);
        }
    }
}
