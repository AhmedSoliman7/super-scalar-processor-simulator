package memory;

public class MemoryHandler {
	Cache[] instructionCaches;
	Cache[] dataCaches;
	int numberOfCaches;
	MainMemory mainMemory;
	
	public MemoryHandler(int numberOfCaches) {
		this.numberOfCaches = numberOfCaches;
		
		mainMemory = new MainMemory();
		instructionCaches = new Cache[numberOfCaches];
		dataCaches = new Cache[numberOfCaches];
		
		configureMemoryHierarchy(instructionCaches);
		configureMemoryHierarchy(dataCaches);
	}
	
	private void configureMemoryHierarchy(Cache[] caches) {
		for(int i = numberOfCaches - 1; i >= 0; i--) {
			if(i == numberOfCaches - 1)
				caches[i] = new Cache(mainMemory);
			else
				caches[i] = new Cache(caches[i + 1]);
		}
	}
	
	public ReturnPair<Short> read(short address) {
		return dataCaches[0].fetch(address);
	}
	
	public short write(short address, short value) {
		return dataCaches[0].write(address, value);
	}
	
	public Cache[] getInstructionCaches() {
		return instructionCaches;
	}

	public Cache[] getDataCaches() {
		return dataCaches;
	}

	public MainMemory getMainMemory() {
		return mainMemory;
	}
}
