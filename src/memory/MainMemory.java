package memory;

public class MainMemory extends Storage{
	static final int SIZE = 64 * 1024 / 2; // memory size in words
	
	short[] data;
	
	public MainMemory() {
		data = new short[SIZE];
	}
	
	@Override
	public short fetch(short address) {
		return data[address];
	}

	@Override
	public void write(short address, short value) {
		data[address] = value;
	}
}
