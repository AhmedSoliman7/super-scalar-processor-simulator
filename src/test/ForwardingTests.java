package test;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.junit.Test;

import main.ProcessorBuilder;
import units.Processor;

public class ForwardingTests {
	static final String USR_FILE_NAME = "testFile.usr";
	
	@Test
	public void RAW1() throws FileNotFoundException {
		ArrayList<String> program = new ArrayList<String>();
		program.add("ADDI r1, r0, 20");
		program.add("SW r1, r0, 0");
		program.add("LW r3, r0, 0");
		program.add("ADD r4, r3, r1");
		TestsInitializer.initGivenAssembly(program);
		TestsInitializer.initUserInput2();
		
		ProcessorBuilder.buildProcessor(new FileInputStream(USR_FILE_NAME));
		Processor processor = ProcessorBuilder.getProcessor();
		
		for(int i = 0; i < 100; i++)
			processor.runClockCycle();
		
		assertEquals(
				"The ADD result stored in r3 should be 30.",
				40,
				processor.getRegisterFile().getRegisterValue((byte) 4));
		
		TestsInitializer.clean();
	}
	
	@Test
	public void RAW2() throws FileNotFoundException {
		ArrayList<String> program = new ArrayList<String>();
		program.add("ADDI r1, r0, 20");
		program.add("ADD r4, r1, r1");
		TestsInitializer.initGivenAssembly(program);
		TestsInitializer.initUserInput2();
		
		ProcessorBuilder.buildProcessor(new FileInputStream(USR_FILE_NAME));
		Processor processor = ProcessorBuilder.getProcessor();
		
		for(int i = 0; i < 100; i++)
			processor.runClockCycle();
		
		assertEquals(
				"The ADD result stored in r3 should be 30.",
				40,
				processor.getRegisterFile().getRegisterValue((byte) 4));
		
		TestsInitializer.clean();
	}
}
