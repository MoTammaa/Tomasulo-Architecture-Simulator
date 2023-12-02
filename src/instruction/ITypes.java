package instruction;

public enum ITypes {
    ADD("ADD"),  // ADD R1, R2, R3      or    ADD F1, F2, F0
    SUB("SUB"),     // SUB R1, R2, R3      or    SUB F1, F2, F0
    MUL("MUL"),     // MUL R1, R2, R3      or    MUL F1, F2, F0
    DIV("DIV"),     // DIV R1, R2, R3      or    DIV F1, F2, F0
    ADDI("ADDI"),   // ADDI R1, R2, 100    or    ADDI F1, F2, 100
    SUBI("SUBI"),   // SUBI R1, R2, 100    or    SUBI F1, F2, 100
    MULI("MULI"),   // MULI R1, R2, 100    or    MULI F1, F2, 100
    DIVI("DIVI"),   // DIVI R1, R2, 100    or    DIVI F1, F2, 100
    LOAD("LOAD"),   // LOAD R1, R2, 100    or    LOAD F1, R2, 100
    STORE("STORE"),     // STORE R1, 100(R0)   or    STORE R3, 200
    BNEZ("BNEZ"),       // BNEZ R1, 100     or    BNEZ F1, 100   // NOT SURE IF 2nd PARAMETER IS OFFSET OR LABEL
    L_D("L.D"),         // L.D F1, 100(R2) or    L.D F1, 300
    S_D("S.D");         // S.D F1, 100(R2) or    S.D F1, 300

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
