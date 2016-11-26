package units;

import main.ProcessorBuilder;

public class ReorderBuffer {

	private ReorderBufferEntry[] entries;
	private short maxSize, head, tail;
	
	public ReorderBuffer(short maxSize) {
		this.maxSize = maxSize;
		entries = new ReorderBufferEntry[maxSize];
		tail = -1;
	}

	private boolean isEmpty() {
		return head == tail;
	}

	public boolean isFull() {
		return (tail + 1) % maxSize == head;
	}
	
	public short nextEntryIndex(){
		tail++;
		tail %= maxSize;
		entries[tail] = new ReorderBufferEntry(true);
		return tail;
	}
	
	public ReorderBufferEntry getEntry(int index){
		return entries[index];
	}

	public void commit() {			//TODO handle JMP, JALR, RET
		if(!isEmpty() && entries[head].isReady()){
			ReorderBufferEntry robHead = entries[head]; 
			if(robHead.getInstructionType() == 4) {	//branch
				if(robHead.getValue() == 0){			//mispredicted branch
					ProcessorBuilder.getProcessor().clear();
					//fetch correct branch
				}
			}
			else if(robHead.getInstructionType() == 2) {	//store
				ProcessorBuilder.getProcessor().getMemoryUnit().write(robHead.getDestination(), robHead.getValue());
			}
			else {
				ProcessorBuilder.getProcessor().getRegisterFile().setRegisterValue((byte)robHead.getDestination(), robHead.getValue());
				if(ProcessorBuilder.getProcessor().getRegisterFile().getRegisterStatus((byte)robHead.getDestination()) == head){
					ProcessorBuilder.getProcessor().getRegisterFile().setRegisterStatus((byte)robHead.getDestination(), (short)-1);//VALID register content
				}
			}
			head++;
		}
	}
	
	public void clear() {
		head = 0;
		tail = -1;
	}
	
	public boolean findMatchingStoreAddress(short address, short end){
		for(short i = end; i >= head; --i){
			if(entries[i].getInstructionType() == 2 && entries[i].getDestination() == address){
				return true;
			}
		}
		return false;
	}
	
	public void flush() {
		for(int i = head; i <= tail; ++i) {
			entries[i].flush();
		}
	}
}