package memory;

public abstract class CacheBlock {
	short[] data;
	short tag;
	int LRUCounter;
	
	public CacheBlock(int blockSize, short tag, int LRUCounter) {
		data = new short[blockSize];
		this.tag = tag;
		this.LRUCounter = LRUCounter;
	}
	
	public abstract void writeToMemory();
	public abstract void updateValue(short address, short value);
}
