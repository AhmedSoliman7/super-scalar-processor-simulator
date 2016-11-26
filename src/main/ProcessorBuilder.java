package main;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

import memory.MemoryHandler;
import memory.WritingPolicy;
import units.Processor;
import units.ReorderBuffer;

public class ProcessorBuilder {
	
	static Processor processor;

	Scanner sc;
	
	private void buildMemory() throws Exception {
		System.out.println("Memory configurations:");
		System.out.println("What is the number of cache levels?");
		
		int numberOfCaches = sc.nextInt();
		
		if(numberOfCaches <= 0) {
			throw new Exception("Invalid number of caches.");
		}
		
		System.out.println("What is the number of cycles required to access the main memory?");
		short memoryAccessTime = sc.nextShort();
		
		MemoryHandler memory = new MemoryHandler(numberOfCaches, memoryAccessTime);
		
		System.out.println("Please enter the configurations for every cache level in the following format:");
		System.out.println("S L M W A");
		System.out.println("Where S is the cache size, L is the block size, "
				+ "M is the cache associativity, W is the writing policy (0 => write back, 1 => write through)"
				+ " and A is the number of cycles to access this cache level.");
		
		for(int cache = 1; cache <= numberOfCaches; cache++) {
			System.out.printf("Cache level %d\n", cache);
			short s = sc.nextShort();
			short l = sc.nextShort();
			short m = sc.nextShort();
			WritingPolicy policy = sc.nextInt() == 0 ? WritingPolicy.WRITE_BACK : WritingPolicy.WRITE_THROUGH;
			short a = sc.nextShort();
			
			memory.configureCache(cache, s, l, m, policy, a);
		}
		
		System.out.println("Done configuring the memory hierarchy.");
		processor.setMemoryUnit(memory);
	}
	
	private void buildHardwareOrganization() {
		System.out.println("Hardware Organization Configurations:");
		System.out.println("Please enter the pipeline width (the number of instructions that can be issued to the reservation stations "
				+ "simultaneously):");
		int pipelineWidth = sc.nextInt();
		processor.setPipelineWidth(pipelineWidth);
		
		System.out.println("Please specify the size of the instruction buffer (queue):");
		int instructionBuffer = sc.nextInt();
		processor.setInstructionQueueMaxSize(instructionBuffer);
		
		System.out.println("Please enter the number of ROB entries");
		int ROBEntries = sc.nextInt();
		processor.setROB(new ReorderBuffer((short) ROBEntries, processor));
		
		int[] countRS = processor.getCountReservationStation();
		
		System.out.println("Please enter the number of LOAD reservation stations");
		countRS[0] = sc.nextInt();
		System.out.println("Please enter the number of STORE reservation stations");
		countRS[1] = sc.nextInt();
		System.out.println("Please enter the number of ADD reservation stations");
		countRS[2] = sc.nextInt();
		System.out.println("Please enter the number of MULT reservation stations");
		countRS[3] = sc.nextInt();
		System.out.println("Please enter the number of NAND reservation stations");
		countRS[4] = sc.nextInt();
		
		System.out.println("Done configuring hardware organization.");
	}
	
	private void initializeMemory() throws FileNotFoundException {
		System.out.println("Please enter the path to the assembly program file.");
		sc.nextLine();
		String filePath = sc.nextLine();
		
		System.out.println("What is the initial address in the memory to write the program?");
		short initAddress = sc.nextShort();
		
		Parser parser = new Parser();
		parser.readProgram(filePath, initAddress, processor.getMemoryUnit());
		
		System.out.println("How many entries to you want to initialize into the memory?");
		int memoryEntries = sc.nextInt();
		if(memoryEntries > 0) {
			System.out.println("Enter the memory initializations in the following format:");
			System.out.println("Address Value");
		}
		
		for(int entry = 1; entry <= memoryEntries; entry++) {
			System.out.printf("Memory entry #%d ", entry);
			short address = sc.nextShort();
			short value = sc.nextShort();
			
			processor.getMemoryUnit().initializeMainMemoryEntry(address, value);
		}
		
		System.out.println("Done initializing the memory.");
	}
	
	public Processor buildProcessor(InputStream in) throws Exception {
		processor = new Processor();
		
		sc = new Scanner(in);
		
		buildMemory();
		buildHardwareOrganization();
		initializeMemory();
		
		sc.close();
		
		return processor;	
	}
	
	public static Processor getProcessor() {
		return processor;
	}
	
	public static void main(String[] args) throws Exception {
		Processor p = new ProcessorBuilder().buildProcessor(System.in);
	}
}
