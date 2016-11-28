package memory;

import main.ProcessorBuilder;
import units.Processor;

public class MemoryHandler {
	Cache[] instructionCaches;
	Cache[] dataCaches;
	int numberOfCaches;
	MainMemory mainMemory;
	
	public MemoryHandler(int numberOfCaches, short memoryAccessTime) {
		this.numberOfCaches = numberOfCaches;
		
		mainMemory = new MainMemory(memoryAccessTime);
		instructionCaches = new Cache[numberOfCaches];
		dataCaches = new Cache[numberOfCaches];
		
		configureMemoryHierarchy(instructionCaches);
		configureMemoryHierarchy(dataCaches);
	}
	
	public void configureCache(int cacheLevel, short size, short blockSize, short associativity, WritingPolicy writingPolicy, short accessTime) {
		instructionCaches[cacheLevel - 1].configureCache(size,
				blockSize,
				associativity,
				writingPolicy,
				accessTime);
		
		dataCaches[cacheLevel - 1].configureCache(size,
				blockSize,
				associativity,
				writingPolicy,
				accessTime);
	}
	
	public double getAMAT(){
		return getInstructionAMAT() + (getDataAMAT() == 0 ? 0 : (ProcessorBuilder.getProcessor().getLoadAndStoreInstructions() * 1.0
				/ ProcessorBuilder.getProcessor().getInstructionCompleted()) * getDataAMAT());
	}
	
	
	public double getInstructionAMAT(){
		double AMAT = 0;
		double missRatio = 1;
		for(int i = 0; i < instructionCaches.length; i++) {
			AMAT += missRatio * instructionCaches[i].accessTime;
			missRatio *= (1 - ProcessorBuilder.getProcessor().getInstructionCacheHitRatio(i) / 100.0);
		}
		AMAT += (missRatio * mainMemory.accessTime);
		return AMAT;
	}
	
	public double getDataAMAT(){
		double AMAT = 0;
		double missRatio = 1;
		for(int i = 0; i < dataCaches.length; i++) {
			AMAT += missRatio * dataCaches[i].accessTime;
			missRatio *= (1 - ProcessorBuilder.getProcessor().getDataCacheHitRatio(i) / 100.0);
		}
		AMAT += (missRatio * mainMemory.accessTime);
		return AMAT;
	}
	
	public void initializeMainMemoryEntry(short address, short value) {
		this.mainMemory.write(address, value);
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
	
	public ReturnPair<Short> fetchInstruction(short address) {
		return instructionCaches[0].fetch(address);
	}
	
	public Cache[] getDataCaches() {
		return dataCaches;
	}
	
	public Cache[] getInstructionCaches() {
		return instructionCaches;
	}

	public MainMemory getMainMemory() {
		return mainMemory;
	}
}