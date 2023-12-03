package instruction;

public class InstructionStatus {
    private Integer issue;
    private Integer executionStart;
    private Integer executionComplete;
    private Integer writeBack;
    
    public void setIssue(int currentCycle) {
    	this.issue=currentCycle;
    }
    public void setExecutionStart(int currentCycle) {
    	this.executionStart=currentCycle;
    }
    public void setExecutionComplete(int currentCycle) {
    	this.executionComplete=currentCycle;
    }
    public void setWriteBack(int currentCycle) {
    	this.writeBack=currentCycle;
    }
    public Integer getIssue() {
    	return this.issue;
    }
    public Integer getExecutionStart() {
    	return this.executionStart;
    }
    public Integer getExecutionComplete() {
    	return this.executionComplete;
    }
    public Integer getWriteBack() {
    	return this.writeBack;
    }
    @Override
    public String toString() {
    	return "[Iss: "+this.issue+" St: "+this.executionStart+" En: "+this.executionComplete+" WB: "+this.writeBack + "]";
    }

}
