package main;

import memory.Cache;
import units.Processor;

public class Simulator {

	private Processor processor;

	public Simulator(){
		processor = ProcessorBuilder.buildProcessor(System.in);
	}
	
	public void run(){
		
		while(!processor.isTerminated())
			processor.runClockCycle();
		
		System.out.printf("Number of instructions completed is %d\n", processor.getInstructionCompleted());
		System.out.printf("Number of branches encountered is %d\n", processor.getBranchesEncountered());
		System.out.printf("Number of cycles spanned is %d\n", processor.getTimer());
		System.out.printf("Data cache:\n");
		Cache dataCache [] = processor.getMemoryUnit().getDataCaches();
		for(int i = 1; i <= dataCache.length; i++) {
			System.out.printf(
					"Level %d: %d read hits, %d read misses, %d write hits, %d write misses, hit ratio: %.2f%%.\n", i,
					dataCache[i - 1].getReadHits(), dataCache[i - 1].getReadMisses(), dataCache[i - 1].getWriteHits(),
					dataCache[i - 1].getWriteMisses(), processor.getDataCacheHitRatio(i - 1));
		}
		System.out.printf("Instruction cache:\n");
		Cache instructionCache[] = processor.getMemoryUnit().getInstructionCaches();
		for (int i = 1; i <= instructionCache.length; i++) {
			System.out.printf(
					"Level %d: %d read hits, %d read misses, %d write hits, %d write misses, hit ratio: %.2f%%.\n", i,
					instructionCache[i - 1].getReadHits(), instructionCache[i - 1].getReadMisses(),
					instructionCache[i - 1].getWriteHits(), instructionCache[i - 1].getWriteMisses(),
					processor.getInstructionCacheHitRatio(i - 1));
		}
		
		System.out.printf("Number of branch misspredictions: %d with percentage of %.2f%%\n",
				processor.getBranchesMisspredictions(), processor.getMispredictedBranchesPercentage());
		
		System.out.printf("Time spent to access memory: %d\n", processor.getTimeSpentToAccessMemory());
		
		System.out.printf("AMAT: %.2f\n", processor.getMemoryUnit().getAMAT());
		System.out.printf("IPC: %.2f\n", processor.getIPC());
	}
	
	public Processor getProcessor() {
		return processor;
	}
}
