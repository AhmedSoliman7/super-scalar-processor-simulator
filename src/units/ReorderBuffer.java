package units;

public class ReorderBuffer {

	private ReorderBufferEntry[] entries;
	private int maxSize, head, tail;

	public ReorderBuffer(int maxSize) {
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
	
	public int getNextEntryIndex(){
		return (tail + 1) % maxSize;
	}
	
	public ReorderBufferEntry getEntry(int index){
		return entries[index];
	}

	public void commit() {
		// TODO
	}
}