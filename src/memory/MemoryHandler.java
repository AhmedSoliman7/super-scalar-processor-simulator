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
}
