package test;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.junit.Test;

import main.ProcessorBuilder;
import reservation_station.LoadReservationStation;
import reservation_station.ReservationStationState;
import units.Processor;
import units.ReorderBufferEntry;

public class ProcessorTests {

	static final String USR_FILE_NAME = "testFile.usr";
	
	@Test
	public void testInstructionFetch() throws Exception {
		TestsInitializer.initAssembly();
		TestsInitializer.initUserInput();

		ProcessorBuilder.buildProcessor(new FileInputStream(USR_FILE_NAME));
		
		ProcessorBuilder.getProcessor().fetchInstruction();
		
		for(int i = 0; i < 56; i++)
			ProcessorBuilder.getProcessor().getInstructionInFetch().decrementCycles();
		
		assertEquals(
				"Instruction should be ready after 56 clock cycles.",
				true,
				ProcessorBuilder.getProcessor().getInstructionInFetch().isReady());
		
		assertEquals(
				"Instruction queue should be still empty.",
				0,
				ProcessorBuilder.getProcessor().getInstructionQueue().size());
		
		ProcessorBuilder.getProcessor().fetchInstruction();
		
		assertEquals(
				"Instruction queue should have one fetched instruction.",
				1,
				ProcessorBuilder.getProcessor().getInstructionQueue().size());
		
		ProcessorBuilder.getProcessor().fetchInstruction();
		
		assertEquals(
				"Instruction queue should have two fetched instructions because the second one was cached in L1.",
				2,
				ProcessorBuilder.getProcessor().getInstructionQueue().size());
		
		TestsInitializer.clean();
	}
	
	@Test
	public void testInstructionCycle() throws FileNotFoundException {
		TestsInitializer.initAssembly2();
		TestsInitializer.initUserInput2();
		
		ProcessorBuilder.buildProcessor(new FileInputStream(USR_FILE_NAME));
		Processor processor = ProcessorBuilder.getProcessor();
		
		assertEquals(
				"PC should be pointing to first instruction",
				100,
				processor.getPC());
		
		for(int i = 0; i < 5; i++)
			processor.runClockCycle();
		
		assertEquals(
				"PC should be pointing to second instruction",
				101,
				processor.getPC());
		
		assertEquals(
				"Instruction queue should be still empty.",
				0,
				processor.getInstructionQueue().size());
		
		processor.runClockCycle();
		
		assertEquals(
				"Instruction queue should have first instruction in the queue.",
				1,
				processor.getInstructionQueue().size());
		
		assertEquals(
				"PC should be pointing to third instruction",
				102,
				processor.getPC());
		
		processor.runClockCycle();
		
		assertEquals(
				"PC should be pointing to third instruction",
				103,
				processor.getPC());
		
		assertEquals(
				"Instruction queue should still have instruction in the queue.",
				1,
				processor.getInstructionQueue().size());
		
		LoadReservationStation lrs = (LoadReservationStation) processor.getReservationStations()[0];
		
		assertEquals(
				"The load reservation station should be busy.",
				true,
				lrs.isBusy());
		
		assertEquals(
				"There should be no ROB taking the LRS.",
				-1,
				lrs.getQj());
		
		assertEquals(
				"The base address is ready.",
				0,
				lrs.getVj());
		
		assertEquals(
				"The base address is ready.",
				32,
				lrs.getAddress());
		
		assertEquals(
				"the LRS should be in EXEC state.",
				ReservationStationState.EXEC,
				lrs.getState());
		
		assertEquals(
				"the LRS start time should be 6.",
				6,
				lrs.getStartTime());
		
		ReorderBufferEntry ROBentry = processor.getROB().getEntry(0);
		
		assertEquals(
				6,
				ROBentry.getDestination());
		
		assertEquals(
				0,
				processor.getRegisterFile().getRegisterStatus((byte) 6));
		
		processor.runClockCycle();
		
		// TODO I1 starting exec, I2 issuing
		
		processor.runClockCycle();
		
		// TODO I1 exec, I2 exec
		
		processor.runClockCycle();
		
		// TODO I1 exec, I2 exec
		
		processor.runClockCycle();
		
		// TODO I1 exec, I2 exec
		
		processor.runClockCycle();
		
		// TODO I1 exec, I2 exec
		
		processor.runClockCycle();
		
		assertEquals(
				ReservationStationState.WRITE,
				lrs.getState());
		
		assertEquals(
				ReservationStationState.EXEC,
				processor.getReservationStations()[1].getState());
		
		assertEquals(
				1,
				processor.getInstructionQueue().size());
		
		processor.runClockCycle();
		
		assertEquals(
				ReservationStationState.COMMIT,
				lrs.getState());
		
		assertEquals(
				ReservationStationState.WRITE,
				processor.getReservationStations()[1].getState());
		
		assertEquals(
				false,
				lrs.isBusy());
		
		assertEquals(
				true,
				ROBentry.isReady());
		
		assertEquals(
				2,
				ROBentry.getValue());
		
		assertEquals(
				0,
				processor.getRegisterFile().getRegisterValue((byte) 6));
		
		assertEquals(
				0,
				processor.getRegisterFile().getRegisterStatus((byte) 6));
		
		processor.runClockCycle();
		
		assertEquals(
				ReservationStationState.COMMIT,
				processor.getReservationStations()[1].getState());
		
		assertEquals(
				2,
				processor.getRegisterFile().getRegisterValue((byte) 6));
		
		assertEquals(
				-1,
				processor.getRegisterFile().getRegisterStatus((byte) 6));
		
		processor.runClockCycle();
		
		assertEquals(
				3,
				processor.getRegisterFile().getRegisterValue((byte) 2));
		
		assertEquals(
				-1,
				processor.getRegisterFile().getRegisterStatus((byte) 2));
		
		assertEquals(
				2,
				processor.getRegisterFile().getRegisterStatus((byte) 3));
		
		TestsInitializer.clean();
	}
}
