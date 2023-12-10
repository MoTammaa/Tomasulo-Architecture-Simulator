package registerFile;

public class RegisterFile {

    private Register[] integerRegs;
    private Register[] floatingRegs;
    private Register branch; // value is the output of the Branch (0 or 1)
                             // status is that if there is a branch instruction in the pipeline 0/1

    public RegisterFile() {
        integerRegs = new Register[32];
        floatingRegs = new Register[32];
        branch = new Register("B");
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
    public void setR(int index, Long value) {
        integerRegs[index].setValue(value.toString());
    }
    public void setF(int index, Double value) {
        floatingRegs[index].setValue(value.toString());
    }
    public void setBTrue() {
        branch.setValue("1");
    }
    public void setBFalse() {
        branch.setValue("0");
    }
    public Long getB() {
        return Long.parseLong(branch.getValue());
    }

    public String getRegister(String registerName) { // registerName = "R0" or "F0" // Floating or Integers
        if (registerName.charAt(0) == 'R') {
            return integerRegs[Integer.parseInt(registerName.substring(1))].getValue();
        } else if ( registerName.charAt(0) == 'B') {
            return branch.getValue();
        } else {
            return floatingRegs[Integer.parseInt(registerName.substring(1))].getValue();
        }
    }

    public void setRegisterStatus(String registerName, String registerStatus) { // registerName = "R0" or "F0" // Floating or Integers
        if (registerName.charAt(0) == 'R') {
            integerRegs[Integer.parseInt(registerName.substring(1))].setRegisterStatus(registerStatus);
        } else if ( registerName.charAt(0) == 'B') {
            branch.setRegisterStatus(registerStatus);
        } else {
            floatingRegs[Integer.parseInt(registerName.substring(1))].setRegisterStatus(registerStatus);
        }
    }
    public void setRegisterValue(String registerName, String registerValue) { // registerName = "R0" or "F0" // Floating or Integers
        if (registerName.charAt(0) == 'R') {
            integerRegs[Integer.parseInt(registerName.substring(1))].setValue(registerValue);
        } else if ( registerName.charAt(0) == 'B') {
            branch.setValue(registerValue);
        } else {
            floatingRegs[Integer.parseInt(registerName.substring(1))].setValue(registerValue);
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

    public boolean isReady(String rs) {
        if (rs.charAt(0) == 'R') {
            return integerRegs[Integer.parseInt(rs.substring(1))].isReady();
        } else {
            return floatingRegs[Integer.parseInt(rs.substring(1))].isReady();
        }
    }

    public String getQ(String rs) {
        try {
            if (rs.charAt(0) == 'R') {
                return integerRegs[Integer.parseInt(rs.substring(1))].getRegisterStatus();
            } else if (rs.charAt(0) == 'B') {
                return branch.getRegisterStatus();
            } else {
                return floatingRegs[Integer.parseInt(rs.substring(1))].getRegisterStatus();
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid Register Name: " + rs);
        }
    }

    public boolean isThereBranchIssued() {
        return branch.isReady();
    }

    public String[][][] getTableData() {
        String[][][] data = new String[2][32][3];
        for (int i = 0; i < 32; i++) {
            data[1][i][0] = "R" + i;
            data[1][i][1] = integerRegs[i].getRegisterStatus();
            data[1][i][2] = integerRegs[i].getValue();
            data[0][i][0] = "F" + i;
            data[0][i][1] = floatingRegs[i].getRegisterStatus();
            data[0][i][2] = floatingRegs[i].getValue();
        }
        return data;
    }
}
