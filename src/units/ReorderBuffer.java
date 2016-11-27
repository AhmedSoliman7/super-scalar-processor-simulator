package units;

import main.ProcessorBuilder;

public class ReorderBuffer {

	private static final short VALID = -1;
	private ReorderBufferEntry[] entries;
	private short maxSize, head, tail;
	private int countEntries;
	private short writingCounter; 
	
	public ReorderBuffer(short maxSize) {
		this.maxSize = maxSize;
		entries = new ReorderBufferEntry[maxSize];
		writingCounter = -1;
	}

	private boolean isEmpty() {
		return countEntries == 0;
	}

	public boolean isFull() {
		return countEntries == maxSize;
	}
	
	public short nextEntryIndex(){
		entries[tail] = new ReorderBufferEntry(true);
		short retTail = tail;
		tail++;
		tail %= maxSize;
		countEntries++;
		return retTail;
	}
	
	public ReorderBufferEntry getEntry(int index){
		return entries[index];
	}

	public void commit() {			//TODO handle JMP, JALR, RET
		if(!isEmpty() && entries[head].isReady()){
			ReorderBufferEntry robHead = entries[head]; 
			if(robHead.getInstructionType() == InstructionType.BEQ) {
				if(robHead.getValue() == 0){				// misprediction
					ProcessorBuilder.getProcessor().clear();
					ProcessorBuilder.getProcessor().updatePC(robHead.getDestination());
				}
			}
			else if(robHead.getInstructionType() == InstructionType.STORE) {
				if(writingCounter > 0) {
					writingCounter--;
					return;
				}
				
				if(writingCounter == -1) {
					writingCounter = ProcessorBuilder.getProcessor().getMemoryUnit().write(robHead.getDestination(), robHead.getValue());
					return;
				}
				
				writingCounter = -1;
			}
			else {
				ProcessorBuilder.getProcessor().getRegisterFile().setRegisterValue((byte)robHead.getDestination(), robHead.getValue());
				if(ProcessorBuilder.getProcessor().getRegisterFile().getRegisterStatus((byte)robHead.getDestination()) == head){
					ProcessorBuilder.getProcessor().getRegisterFile().setRegisterStatus((byte)robHead.getDestination(), VALID);
				}
			}
		
			head++;
			head %= maxSize;
			countEntries--;
		}
	}
	
	public void clear() {
		head = 0;
		tail = 0;
	}
	
	public boolean findMatchingStoreAddress(short address, short end){
		for(short i = 0; i < countEntries; i++){
			short idx = (short) ((head + i) % maxSize);
			if(idx == end)
				break;
			if(entries[idx].getInstructionType() == InstructionType.STORE && entries[idx].getDestination() == address){
				return true;
			}
		}
		return false;
	}
	
	public void flush() {
		for(short i = 0; i < countEntries; i++){
			short idx = (short) ((head + i) % maxSize);
			
			entries[idx].flush();
		}
	}
}