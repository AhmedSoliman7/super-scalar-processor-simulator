package test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

import org.junit.Test;

import main.Simulator;
import memory.Cache;

public class SimulatorTests {
	
	static final String USR_FILE_NAME = "testFile.usr";
	static final String SIMULATOR_FILE = "simulator.out";
	
	
	@Test
	public void testSimulator() throws FileNotFoundException {
		ArrayList<String> program = new ArrayList<String>();
		program.add("ADDI r7, r7, 50");
		program.add("ADDI r7, r7, 57");
		program.add("ADDI r3, r3, 4");
		program.add("ADDI r2, r2, 2");
		program.add("ADDI r1, r1, 1");
		program.add("JALR r6, r7");
		program.add("JMP r0, 6");
		program.add("BEQ r3, r0, 1");
		program.add("JMP r0, 1");
		program.add("RET r6");
		program.add("ADDI r3, r3, -1");
		program.add("MULT r1, r1, r2");
		program.add("JMP r0, -6");
		program.add("SW r1, r0, 15");
		program.add("LW r5, r0, 15");
		
		TestsInitializer.initGivenAssembly(program);
		TestsInitializer.initUserInput();
		
		System.setIn(new FileInputStream(USR_FILE_NAME));
		
		
		Simulator simulator = new Simulator();
		
		File simulatorFile = new File(SIMULATOR_FILE);
		System.setOut(new PrintStream(simulatorFile));
		simulator.run();
		
		Scanner sc = new Scanner(simulatorFile);
		
		ArrayList<String> simulatorOutput = new ArrayList<String>();
		while(sc.hasNextLine()) {
			simulatorOutput.add(sc.nextLine());
		}
		
		int numCaches = simulator.getProcessor().getMemoryUnit().getDataCaches().length;
		
		assertEquals(
				9 + 2 * numCaches,
				simulatorOutput.size());
		
		int outputIndex = 0;
		
		assertEquals(
				String.format("Number of instructions completed is %d", simulator.getProcessor().getInstructionCompleted()),
				simulatorOutput.get(outputIndex++));
		
		assertEquals(
				String.format("Number of branches encountered is %d", simulator.getProcessor().getBranchesEncountered()),
				simulatorOutput.get(outputIndex++));
		
		assertEquals(
				String.format("Number of cycles spanned is %d", simulator.getProcessor().getTimer()),
				simulatorOutput.get(outputIndex++));
		
		outputIndex++;
		
		Cache[] dataCache = simulator.getProcessor().getMemoryUnit().getDataCaches();
		Cache[] InstructionCache = simulator.getProcessor().getMemoryUnit().getInstructionCaches();
		
		for(int i = 0; i < numCaches; i++) {
			assertEquals(
					String.format(
							"Level %d: %d read hits, %d read misses, %d write hits, %d write misses, hit ration: %.2f%%.",
							i + 1, dataCache[i].getReadHits(), dataCache[i].getReadMisses(),
							dataCache[i].getWriteHits(), dataCache[i].getWriteMisses(),
							simulator.getProcessor().getDataCacheHitRatio(i)),
					simulatorOutput.get(outputIndex++));
		}
		
		outputIndex++;
		
		for(int i = 0; i < numCaches; i++) {
			assertEquals(
					String.format("Level %d: %d read hits, %d read misses, %d write hits, %d write misses, hit ration: %.2f%%.",
							i + 1,
							InstructionCache[i].getReadHits(),
							InstructionCache[i].getReadMisses(),
							InstructionCache[i].getWriteHits(),
							InstructionCache[i].getWriteMisses(),
							simulator.getProcessor().getInstructionCacheHitRatio(i)),
					simulatorOutput.get(outputIndex++));
		}
		
		assertEquals(
				String.format("Number of branch misspredictions: %d with percentage of %.2f%%",
						simulator.getProcessor().getBranchesMisspredictions(),
						simulator.getProcessor().getMispredictedBranchesPercentage()),
				simulatorOutput.get(outputIndex++));
		
		assertEquals(
				String.format("Time spent to access memory: %d",
						simulator.getProcessor().getTimeSpentToAccessMemory()),
				simulatorOutput.get(outputIndex++));
		
		assertEquals(
				String.format("AMAT: %.2f",
						simulator.getProcessor().getMemoryUnit().getAMAT()),
				simulatorOutput.get(outputIndex++));
		
		assertEquals(
				String.format("IPC: %.2f",
						simulator.getProcessor().getIPC()),
				simulatorOutput.get(outputIndex++));
		
		
		simulatorFile.delete();
		TestsInitializer.clean();
	}
}
