package units;

public class ReorderBufferEntry {

	private byte instructionType;
	private short value, destintation;
	private boolean ready;
	
	public boolean isReady(){
		return ready;
	}
	
	public void setReady(boolean value){
		ready = value;
	}
	
	public short getValue(){
		return value;
	}
	
	public void setValue(short value){
		this.value = value;
	}
	
	public void setType(byte type){
		instructionType = type;
	}
	
	public void setDestination(short value){
		destintation = value;
	}
	
	/**
	 * @return opcode of the instruction in the current reorder buffer entry
	 */
	public byte getInstructionType(){
		return instructionType;
	}
	
	public short getDestination(){
		return destintation;
	}
}
