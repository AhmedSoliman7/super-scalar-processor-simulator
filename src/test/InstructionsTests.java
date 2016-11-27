package test;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.junit.Test;

import main.ProcessorBuilder;
import units.Processor;

public class InstructionsTests {
	static final String USR_FILE_NAME = "testFile.usr";
	
	@Test
	public void testADDI() throws FileNotFoundException {
		ArrayList<String> program = new ArrayList<String>();
		program.add("ADDI r6, r1, 20");
		TestsInitializer.initGivenAssembly(program);
		TestsInitializer.initUserInput2();
		
		ProcessorBuilder.buildProcessor(new FileInputStream(USR_FILE_NAME));
		Processor processor = ProcessorBuilder.getProcessor();
		
		for(int i = 0; i < 11; i++)
			processor.runClockCycle();
		
		assertEquals(
				"The ADDI result stored in r6 should be 20.",
				20,
				processor.getRegisterFile().getRegisterValue((byte) 6));
		
		TestsInitializer.clean();
	}
	
	@Test
	public void testADD() throws FileNotFoundException {
		ArrayList<String> program = new ArrayList<String>();
		program.add("ADDI r1, r0, 20");
		program.add("ADDI r2, r0, 10");
		program.add("ADD r3, r1, r2");
		TestsInitializer.initGivenAssembly(program);
		TestsInitializer.initUserInput2();
		
		ProcessorBuilder.buildProcessor(new FileInputStream(USR_FILE_NAME));
		Processor processor = ProcessorBuilder.getProcessor();
		
		for(int i = 0; i < 100; i++)
			processor.runClockCycle();
		
		assertEquals(
				"The ADD result stored in r3 should be 30.",
				30,
				processor.getRegisterFile().getRegisterValue((byte) 3));
		
		TestsInitializer.clean();
	}
	
	@Test
	public void testSUB() throws FileNotFoundException {
		ArrayList<String> program = new ArrayList<String>();
		program.add("ADDI r1, r0, 20");
		program.add("ADDI r2, r0, 10");
		program.add("SUB r3, r1, r2");
		TestsInitializer.initGivenAssembly(program);
		TestsInitializer.initUserInput2();
		
		ProcessorBuilder.buildProcessor(new FileInputStream(USR_FILE_NAME));
		Processor processor = ProcessorBuilder.getProcessor();
		
		for(int i = 0; i < 100; i++)
			processor.runClockCycle();
		
		assertEquals(
				"The SUB result stored in r3 should be 10.",
				10,
				processor.getRegisterFile().getRegisterValue((byte) 3));
		
		TestsInitializer.clean();
	}
	
	@Test
	public void testMULT() throws FileNotFoundException {
		ArrayList<String> program = new ArrayList<String>();
		program.add("ADDI r1, r0, 20");
		program.add("ADDI r2, r0, 10");
		program.add("MULT r3, r1, r2");
		TestsInitializer.initGivenAssembly(program);
		TestsInitializer.initUserInput2();
		
		ProcessorBuilder.buildProcessor(new FileInputStream(USR_FILE_NAME));
		Processor processor = ProcessorBuilder.getProcessor();
		
		for(int i = 0; i < 100; i++)
			processor.runClockCycle();
		
		assertEquals(
				"The MULT result stored in r3 should be 200",
				200,
				processor.getRegisterFile().getRegisterValue((byte) 3));
		
		TestsInitializer.clean();
	}
	
	@Test
	public void testNAND() throws FileNotFoundException {
		ArrayList<String> program = new ArrayList<String>();
		program.add("ADDI r1, r0, 43");
		program.add("ADDI r2, r0, 30");
		program.add("NAND r3, r1, r2");
		TestsInitializer.initGivenAssembly(program);
		TestsInitializer.initUserInput2();
		
		ProcessorBuilder.buildProcessor(new FileInputStream(USR_FILE_NAME));
		Processor processor = ProcessorBuilder.getProcessor();
		
		for(int i = 0; i < 100; i++)
			processor.runClockCycle();
		
		assertEquals(
				"The NAND result stored in r3 should be -11	",
				-11,
				processor.getRegisterFile().getRegisterValue((byte) 3));
		
		TestsInitializer.clean();
	}
	
	@Test
	public void testSW() throws FileNotFoundException {
		ArrayList<String> program = new ArrayList<String>();
		program.add("ADDI r1, r0, 0");
		program.add("ADDI r2, r0, 30");
		program.add("SW r2, r1, 0");
		TestsInitializer.initGivenAssembly(program);
		TestsInitializer.initUserInput2();
		
		ProcessorBuilder.buildProcessor(new FileInputStream(USR_FILE_NAME));
		Processor processor = ProcessorBuilder.getProcessor();
		
		for(int i = 0; i < 100; i++)
			processor.runClockCycle();
		
		assertEquals(
				"The cached SW result should be 30.",
				30,
				processor.getMemoryUnit().
				getDataCaches()[0].
				getSets()[0].
				getBlocks()[0].
				getData()[0]);
		
		TestsInitializer.clean();
	}
	
	@Test
	public void testLW() throws FileNotFoundException {
		ArrayList<String> program = new ArrayList<String>();
		program.add("ADDI r1, r0, 0");
		program.add("LW r2, r1, 0");
		TestsInitializer.initGivenAssembly(program);
		TestsInitializer.initUserInput2();
		
		ProcessorBuilder.buildProcessor(new FileInputStream(USR_FILE_NAME));
		Processor processor = ProcessorBuilder.getProcessor();
		
		processor.getMemoryUnit().getMainMemory().getData()[0] = 100;
		
		for(int i = 0; i < 18; i++)
			processor.runClockCycle();
		
		assertEquals(
				"The cached SW result should be 30.",
				100,
				processor.getRegisterFile().getRegisterValue((byte) 2));
		
		TestsInitializer.clean();
	}
}