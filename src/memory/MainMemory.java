package memory;

public class MainMemory extends Storage{
	static final int SIZE = 64 * 1024 / 2; // memory size in words
	
	short[] data;

	public MainMemory(short accessTime) {
		data = new short[SIZE];
		this.accessTime = accessTime;
	}
	
	@Override
	public ReturnPair<Short> fetch(short address) {
		return new ReturnPair<Short>(this.accessTime, data[address]);
	}

	@Override
	public short write(short address, short value) {
		data[address] = value;
		
		return this.accessTime;
	}
	
	public short[] getData() {
		return data;
	}
}
