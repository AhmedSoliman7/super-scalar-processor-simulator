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
	
	public void setType(byte type){
		instructionType = type;
	}
	
	public void setDestination(short value){
		destintation = value;
	}
	
}
