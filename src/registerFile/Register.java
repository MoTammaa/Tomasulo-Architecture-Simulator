package registerFile;

public class Register {
    private final String registerName;
    private String registerStatus;
    private String value;
    private String type; // "integer" or "floating"

    public Register(String registerName, String registerStatus, String value, String type) {
        this.registerName = registerName;
        this.registerStatus = registerStatus;  //"valid" or The name of the reservation station Tag that will write to this register
        this.value = value;
        this.type = type;
    }
    public Register(String registerName, String type) {
        this.registerName = registerName;
        this.registerStatus = "valid";
        this.value = "0";
        this.type = type;
    }
    public Register(String registerName) {
        this.registerName = registerName;
        this.registerStatus = "valid";
        this.value = "0";
        this.type = (registerName.charAt(0) == 'R')? "integer": "floating";
    }

    public void setRegisterStatus(String registerStatus) {
        this.registerStatus = registerStatus;
    }
    public String getRegisterStatus() {
        return registerStatus;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }

    public String getRegisterName() {
        return registerName;
    }

    @Override
    public String toString() {
        return "(" + registerName + (registerName.length() < 3? " ": "") + ": \t" +
               "Status: " + registerStatus + ", \t" +
               "Value: " + value + ")\n";
    }
}
