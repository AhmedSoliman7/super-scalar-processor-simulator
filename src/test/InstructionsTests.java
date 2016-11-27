package test;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.junit.Test;

import main.ProcessorBuilder;
import reservation_station.ReservationStationState;
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
	
	@Test
	public void testBEQ() throws FileNotFoundException {
		ArrayList<String> program = new ArrayList<String>();
		program.add("ADDI r2, r2, 1");
		program.add("ADDI r1, r1, 1");
		program.add("BEQ r1, r2, -2");
		program.add("ADDI r3, r3, 5");
		
		TestsInitializer.initGivenAssembly(program);
		TestsInitializer.initUserInput2();
		
		
		ProcessorBuilder.buildProcessor(new FileInputStream(USR_FILE_NAME));
		Processor processor = ProcessorBuilder.getProcessor();
		
		processor.getMemoryUnit().getMainMemory().getData()[0] = 100;
		
		for(int i = 0; i < 12; i++)
			processor.runClockCycle();
		
		assertEquals(
				false,
				processor.getReservationStations()[4].isBusy());
		
		processor.runClockCycle();
		// clock cycle 13
		
		assertEquals(
				"BEQ is beginning executing.",
				ReservationStationState.EXEC,
				processor.getReservationStations()[4].getState());
		
		assertEquals(
				"Last ADDI hasn't begun yet.",
				false,
				processor.getReservationStations()[5].isBusy());
		
		processor.runClockCycle();
		
		assertEquals(
				"Last ADDI will start EXEC.",
				ReservationStationState.EXEC,
				processor.getReservationStations()[5].getState());
		
		processor.runClockCycle();
		// clock cycle 15
		
		assertEquals(
				"BEQ is beginning writing.",
				ReservationStationState.WRITE,
				processor.getReservationStations()[4].getState());
		
		processor.runClockCycle();
		// clock cycle 16
		
		assertEquals(
				"BEQ is beginning committing.",
				ReservationStationState.COMMIT,
				processor.getReservationStations()[4].getState());
		
		assertEquals(
				"Last ADDI will start WRITE.",
				ReservationStationState.WRITE,
				processor.getReservationStations()[5].getState());
		
		assertEquals(
				"Current PC shouldn't be changed yet",
				105,
				processor.getPC());
		
		processor.runClockCycle();
		// clock cycle 17
		
		assertEquals(
				"Current PC should point to the Branch Address",
				102,
				processor.getPC());
		
		assertEquals(
				"The instruction queue should be flushed after branching.",
				0,
				processor.getInstructionQueue().size());
		
		assertEquals(
				"The reservation stations should be freed.",
				false,
				processor.getReservationStations()[4].isBusy()
				|| processor.getReservationStations()[5].isBusy());
		
		assertEquals(
				"The reservation stations should be freed.",
				0,
				processor.getROB().getCountEntries());
		
		processor.runClockCycle();
		// clock cycle 18
		assertEquals(
				"The ADDI wasn't issued yet.",
				false,
				processor.getReservationStations()[4].isBusy());
		
		assertEquals(
				"ADDI is fetched and added to instruction queue",
				1,
				processor.getInstructionQueue().size());
		
		
		processor.runClockCycle();
		// clock cycle 19
		
		assertEquals(
				"ADDI is issued and about to EXEC",
				ReservationStationState.EXEC,
				processor.getReservationStations()[4].getState());
		
		assertEquals(
				"next BEQ is fetched.",
				1,
				processor.getInstructionQueue().size());
		
		assertEquals(
				"BEQ is not issud yet.",
				false,
				processor.getReservationStations()[5].isBusy());
		
		processor.runClockCycle();
		// clock cycle 20
		
		assertEquals(
				"BEQ is issued and about to EXEC",
				ReservationStationState.EXEC,
				processor.getReservationStations()[5].getState());
			
		processor.runClockCycle();
		processor.runClockCycle();
		

		// clock cycle 22
		
		assertEquals(
				"ADDI is about to commit.",
				ReservationStationState.COMMIT,
				processor.getReservationStations()[4].getState());
		
		processor.runClockCycle();
		// clock cycle 23;
		assertEquals(
				"The result from ADDI in r1 should be 2.",
				2,
				processor.getRegisterFile().getRegisterValue((byte) 1));

		assertEquals(
				"ADDI is about to EXEC.",
				ReservationStationState.EXEC,
				processor.getReservationStations()[4].getState());
		
		processor.runClockCycle();
		// clock cycle 24
		assertEquals(
				"BEQ is about to WRITE.",
				ReservationStationState.WRITE,
				processor.getReservationStations()[5].getState());
		
		processor.runClockCycle();
		processor.runClockCycle();
		// clock cycle 26;
		assertEquals(
				"ADDI is about to COMMIT.",
				ReservationStationState.COMMIT,
				processor.getReservationStations()[4].getState());
		
		assertEquals(
				"The PC with branch not taken should be 105",
				107,
				processor.getPC());
		
		processor.runClockCycle();
		// clock cycle 27
		assertEquals(
				"The result from the last ADDI in r3 should be 5.",
				5,
				processor.getRegisterFile().getRegisterValue((byte) 3));
		
		TestsInitializer.clean();
	}
}