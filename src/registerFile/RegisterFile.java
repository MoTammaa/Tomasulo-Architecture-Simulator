package registerFile;

public class RegisterFile {

    private Register[] integerRegs;
    private Register[] floatingRegs;

    public RegisterFile() {
        integerRegs = new Register[32];
        floatingRegs = new Register[32];
        for (int i = 0; i < 32; i++) {
            integerRegs[i] = new Register("R" + i);
            floatingRegs[i] = new Register("F" + i);
        }
    }



    // getters and setters
    public Long R(int index) {
        return Long.parseLong(integerRegs[index].getValue());
    }
    public Long R(String nameAndOrIndex) {
        if (nameAndOrIndex.charAt(0) == 'F' ) return null;

        if (nameAndOrIndex.charAt(0) == 'R' ) nameAndOrIndex = nameAndOrIndex.substring(1);

        return Long.parseLong(integerRegs[Integer.parseInt(nameAndOrIndex)].getValue());
    }
    public Double F(int index) {
        return Double.parseDouble(floatingRegs[index].getValue());
    }
    public Double F(String nameAndOrIndex) {
        if (nameAndOrIndex.charAt(0) == 'R' ) return null;

        if (nameAndOrIndex.charAt(0) == 'F' ) nameAndOrIndex = nameAndOrIndex.substring(1);

        return Double.parseDouble(floatingRegs[Integer.parseInt(nameAndOrIndex)].getValue());
    }
    public void setR(int index, Integer value) {
        integerRegs[index].setValue(value.toString());
    }
    public void setF(int index, Double value) {
        floatingRegs[index].setValue(value.toString());
    }

    public String getRegister(String registerName) { // registerName = "R0" or "F0" // Floating or Integers
        if (registerName.charAt(0) == 'R') {
            return integerRegs[Integer.parseInt(registerName.substring(1))].getValue();
        } else {
            return floatingRegs[Integer.parseInt(registerName.substring(1))].getValue();
        }
    }

    public Register[] getIntegerRegs() {
        return integerRegs;
    }
    public void setIntegerRegs(Register[] integerRegs) {
        this.integerRegs = integerRegs;
    }
    public Register[] getFloatingRegs() {
        return floatingRegs;
    }
    public void setFloatingRegs(Register[] floatingRegs) {
        this.floatingRegs = floatingRegs;
    }


    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Register File: **************************\n");

        str.append("Floating Point Registers:_________________\n");
        str.append("| Reg Name |  Reg Status  |  Value  |\n");
        for (Register floatingReg : floatingRegs) {
            str.append("|  ").append(floatingReg.toString());
        }
        str.append("______________________________________________________________\n\n");
        str.append("Integer Registers:________________________\n");
        str.append("| Reg Name |  Reg Status  |  Value  |\n");
        for (Register integerReg : integerRegs) {
            str.append("|   ").append(integerReg.toString());
        }
        str.append("______________________________________________________________\n\n");
        return str.toString();
    }

}
