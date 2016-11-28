package units;

import java.beans.IntrospectionException;

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

	public void commit() {
		if(!isEmpty() && entries[head].isReady()){
			ReorderBufferEntry robHead = entries[head];
			
			InstructionType type = robHead.getInstructionType();
			if(type == InstructionType.LOAD || type == InstructionType.STORE) {
				ProcessorBuilder.getProcessor().incrementLoadAndStoreInstructions();
			}
			
			if(type == InstructionType.BEQ){
				ProcessorBuilder.getProcessor().incrementBranchesEncountered();
			}
			
			if(type == InstructionType.BEQ || type == InstructionType.JMP || type == InstructionType.RET) {
				if(robHead.getValue() == 0){
					if(type == InstructionType.BEQ)
						ProcessorBuilder.getProcessor().incrementBranchesMisspredictions();
					ProcessorBuilder.getProcessor().clear();
					ProcessorBuilder.getProcessor().updatePC(robHead.getDestination());
					ProcessorBuilder.getProcessor().incrementInstructionCompleted();
					return;
				}
			}
			else if(type == InstructionType.STORE) {
				if(writingCounter > 0) {
					writingCounter--;
					return;
				}
				
				if(writingCounter == -1) {
					writingCounter = ProcessorBuilder.getProcessor().getMemoryUnit().write(robHead.getDestination(), (short) robHead.getValue());
					ProcessorBuilder.getProcessor().incrementTimeSpentToAccessMemory(writingCounter);
					return;
				}
				
				writingCounter = -1;
			}
			else if(type == InstructionType.JALR) {
				ProcessorBuilder.getProcessor().incrementInstructionCompleted();
				ProcessorBuilder.getProcessor().clear();
				short targetAddress = (short) (robHead.getValue() & ((1 << 16) - 1));
				short regValue = (short) (robHead.getValue() >>> 16);
				
				ProcessorBuilder.getProcessor().updatePC(targetAddress);
				ProcessorBuilder.getProcessor().getRegisterFile().setRegisterValue((byte) robHead.getDestination(), regValue);
				return;
			}
			else {
				ProcessorBuilder.getProcessor().getRegisterFile().setRegisterValue((byte)robHead.getDestination(), (short) robHead.getValue());
				if(ProcessorBuilder.getProcessor().getRegisterFile().getRegisterStatus((byte)robHead.getDestination()) == head){
					ProcessorBuilder.getProcessor().getRegisterFile().setRegisterStatus((byte)robHead.getDestination(), VALID);
				}
			}
			ProcessorBuilder.getProcessor().incrementInstructionCompleted();
			head++;
			head %= maxSize;
			countEntries--;
		}
	}
	
	public void clear() {
		this.countEntries = 0;
		head = 0;
		tail = 0;
		writingCounter = -1;
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
	
	public int getCountEntries() {
		return countEntries;
	}
}