package units;

public class ReorderBuffer {

	private ReorderBufferEntry[] entries;
	private short maxSize, head, tail;
	private Processor processor;
	
	public ReorderBuffer(short maxSize, Processor processor) {
		this.maxSize = maxSize;
		entries = new ReorderBufferEntry[maxSize];
		tail = -1;
		this.processor = processor;
	}

	private boolean isEmpty() {
		return head == tail;
	}

	public boolean isFull() {
		return (tail + 1) % maxSize == head;
	}
	
	public short nextEntryIndex(){
		return (short) ((++tail) % maxSize);
	}
	
	public ReorderBufferEntry getEntry(int index){
		return entries[index];
	}

	public void commit() {			//TODO handle JMP, JALR, RET
		if(!isEmpty() && entries[head].isReady()){
			ReorderBufferEntry robHead = entries[head]; 
			if(robHead.getInstructionType() == 4) {	//branch
				if(robHead.getValue() == 0){			//mispredicted branch
					processor.clear();
					//fetch correct branch
				}
			}
			else if(robHead.getInstructionType() == 2) {	//store
				processor.getMemoryUnit().write(robHead.getDestination(), robHead.getValue());
			}
			else {
				processor.setRegisterValue((byte)robHead.getDestination(), robHead.getValue());
				if(processor.getRegisterStatus((byte)robHead.getDestination()) == head){
					processor.setRegisterStatus((byte)robHead.getDestination(), (short)-1);			//VALID register content
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
}