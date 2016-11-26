package units;

public class ReorderBufferEntry {

	private InstructionType instructionType;
	private short value, destintation;
	private boolean ready;
	private ReorderBufferEntry tempROBEntry;
	
	public ReorderBufferEntry(boolean isOriginal) {
		if(isOriginal) {
			tempROBEntry = new ReorderBufferEntry(false);
		}
	}
	
	public boolean isReady(){
		return ready;
	}
	
	
	public short getValue(){
		return value;
	}
	
	public void setValue(short value){
		if(tempROBEntry != null) {
			tempROBEntry.setValue(value);
			return;
		}
		this.value = value;
		this.ready = true;
	}
	
	/**
	 * @return opcode of the instruction in the current reorder buffer entry
	 */
	public InstructionType getInstructionType(){
		return instructionType;
	}
	
	public void setInstructionType(InstructionType type) {
		instructionType = type;
	}
	
	
	public short getDestination(){
		return destintation;
	}
	
	public void setDestination(short value){
		if(tempROBEntry != null) {
			tempROBEntry.setValue(value);
			return;
		}
		destintation = value;
	}
	
	public void flush() {
		instructionType = tempROBEntry.instructionType;
		destintation = tempROBEntry.destintation;
		value = tempROBEntry.value;
		ready = tempROBEntry.ready;
	}
}
