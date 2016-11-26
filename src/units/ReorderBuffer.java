package units;

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
	
	public short getNextEntryIndex(){
		return (short) ((tail + 1) % maxSize);
	}
	
	public ReorderBufferEntry getEntry(int index){
		return entries[index];
	}

	public void commit() {
		// TODO
	}
}