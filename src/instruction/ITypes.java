package instruction;

public enum ITypes {
    ADD("ADD"),  SUB("SUB"), MUL("MUL"), DIV("DIV"), ADDI("ADDI"),
    SUBI("SUBI"), MULI("MULI"), DIVI("DIVI"), LOAD("LOAD"),
    STORE("STORE"), BNEZ("BNEZ"), L_D("L.D"), S_D("S.D");

    private final String instructionType;

    ITypes(String instructionType) {
        this.instructionType = instructionType;
    }

    public String getInstructionType() {
        return instructionType;
    }

    public static ITypes getInstructionType(String instructionType) {
        for (ITypes iType : ITypes.values()) {
            if (iType.getInstructionType().equals(instructionType)) {
                return iType;
            }
        }
        return null;
    }
}
