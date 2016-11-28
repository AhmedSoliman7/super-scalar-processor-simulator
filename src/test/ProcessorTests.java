package test;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

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
		
		LoadReservationStation lrs = (LoadReservationStation) processor.getReservationStations()[0];
		
		assertEquals(
				"The load reservation station should be still free.",
				false,
				lrs.isBusy());
		
		// clock cycle 7
		processor.runClockCycle();
		
		assertEquals(
				"PC should be pointing to third instruction",
				103,
				processor.getPC());
		
		assertEquals(
				"Instruction queue should still have instruction in the queue.",
				1,
				processor.getInstructionQueue().size());
		
		
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
		
		// clock cycle 8
		processor.runClockCycle();
		
		// clock cycle 9
		processor.runClockCycle();
		
		// clock cycle 10
		processor.runClockCycle();
		
		// clock cycle 11
		processor.runClockCycle();
		
		// clock cycle 12
		processor.runClockCycle();
		
		assertEquals(
				"The MULT reservation station should be still free.",
				false,
				processor.getReservationStations()[6].isBusy());
		
		// clock cycle 13
		processor.runClockCycle();
		
		assertEquals(
				"The MULT issue in cycle 13.",
				true,
				processor.getReservationStations()[6].isBusy());
		
		assertEquals(
				"The LOAD should begin writing in cycle 13.",
				ReservationStationState.WRITE,
				lrs.getState());
		
		assertEquals(
				"The second LOAD should be in EXEC in cycle 13",
				ReservationStationState.EXEC,
				processor.getReservationStations()[1].getState());
		
		// clock cycle 14
		processor.runClockCycle();
		
		assertEquals(
				ReservationStationState.COMMIT,
				lrs.getState());
		
		assertEquals(
				"The second LOAD should finish the EXEC by cycle 14.",
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
		
		assertEquals(
				"The second LOAD should finish the EXEC by cycle 14.",
				-1,
				processor.getReservationStations()[6].getQk());
		
		assertEquals(
				"The second LOAD should finish the EXEC by cycle 14.",
				1,
				processor.getReservationStations()[6].getQj());
		
		assertEquals(
				"The second LOAD should finish the EXEC by cycle 15.",
				-1,
				processor.getReservationStations()[6].getQk());
		
		assertEquals(
				"The second LOAD should finish the EXEC by cycle 15.",
				-1,
				processor.getReservationStations()[4].getQk());
		
		//clock cycle 15
		processor.runClockCycle();
		
		assertEquals(
				"The second LOAD should finish the EXEC by cycle 15.",
				-1,
				processor.getReservationStations()[6].getQj());
		
		
		assertEquals(
				ReservationStationState.COMMIT,
				processor.getReservationStations()[1].getState());
		
		assertEquals(
				2,
				processor.getRegisterFile().getRegisterValue((byte) 6));
		
		assertEquals(
				-1,
				processor.getRegisterFile().getRegisterStatus((byte) 6));
		
		//clock cycle 16
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
		
		
		//clock cycle 17
		processor.runClockCycle();
		
		assertEquals(
				"The second LOAD should finish the EXEC by cycle 14.",
				-1,
				processor.getReservationStations()[6].getQj());
		
		assertEquals(
				"The MULT should finish the EXEC by cycle 14.",
				ReservationStationState.WRITE,
				processor.getReservationStations()[6].getState());
		
		//clock cycle 18
		processor.runClockCycle();
		
		assertEquals(
				"The MULT should finish the EXEC by cycle 14.",
				ReservationStationState.COMMIT,
				processor.getReservationStations()[6].getState());
		
		assertEquals(
				"The MULT should finish the EXEC by cycle 14.",
				0,
				processor.getRegisterFile().getRegisterValue((byte) 3));
		
		//clock cycle 19
		processor.runClockCycle();
		
		assertEquals(
				"The MULT should finish the EXEC by cycle 14.",
				6,
				processor.getRegisterFile().getRegisterValue((byte) 3));
		
		//clock cycle 20
		processor.runClockCycle();
		
		assertEquals(
				"The MULT should finish the EXEC by cycle 14.",
				-1,
				processor.getRegisterFile().getRegisterStatus((byte) 3));
		
		assertEquals(
				-1,
				processor.getReservationStations()[4].getQj());
		
		assertEquals(
				-1,
				processor.getReservationStations()[4].getQk());
		
		assertEquals(
				"The SUB should finish the EXEC by cycle 21.",
				ReservationStationState.WRITE,
				processor.getReservationStations()[4].getState());
		
		assertEquals(
				0,
				processor.getROB().getEntry(3).getValue());
				
		//clock cycle 21
		processor.runClockCycle();
		
		assertEquals(
				ReservationStationState.COMMIT,
				processor.getReservationStations()[4].getState());
		
		assertEquals(
				4,
				processor.getROB().getEntry(3).getValue());
		
		//clock cycle 22
		processor.runClockCycle();
				
		
		
		assertEquals(
				3,
				processor.getRegisterFile().getRegisterValue((byte) 2));
		
		assertEquals(
				6,
				processor.getRegisterFile().getRegisterValue((byte) 3));
		
		for(int i = 0; i < 6; i++)
			processor.runClockCycle();
		
		assertEquals(
				28,
				processor.getRegisterFile().getRegisterValue((byte) 4));

		assertEquals(
				24,			
				processor.getRegisterFile().getRegisterValue((byte) 6));

		assertEquals(
				4,
				processor.getRegisterFile().getRegisterValue((byte) 7));

		TestsInitializer.clean();
	}
	
	@Test
	public void testProtectingRegister0() throws FileNotFoundException {
		ArrayList<String> program = new ArrayList<String>();
		program.add("ADDI r0, r0, 20");
		TestsInitializer.initGivenAssembly(program);
		TestsInitializer.initUserInput2();
		
		ProcessorBuilder.buildProcessor(new FileInputStream(USR_FILE_NAME));
		Processor processor = ProcessorBuilder.getProcessor();
		
		for(int i = 0; i < 20; i++)
			processor.runClockCycle();
		
		assertEquals(
				"Register zero is protected, value was not affected.",
				0,
				processor.getRegisterFile().getRegisterValue((byte) 0));
		
		TestsInitializer.clean();
	}
	
	@Test
	public void testTerminatingProgram() throws FileNotFoundException {
		ArrayList<String> program = new ArrayList<String>();
		program.add("ADDI r7, r7, 50");
		program.add("ADDI r7, r7, 57");
		program.add("ADDI r3, r3, 4");
		program.add("ADDI r1, r1, 1");
		program.add("ADD r4, r1, r3");
		program.add("JALR r6, r7");
		program.add("JMP r4, 0");
		program.add("BEQ r3, r0, 3");
		program.add("ADDI r3, r3, -1");
		program.add("MULT r1, r4, r1");
		program.add("JMP r0 -4");
		program.add("RET r6");
		program.add("SW r1, r0, 15");
		program.add("LW r5, r0, 15");
		
		TestsInitializer.initGivenAssembly(program);
		TestsInitializer.initUserInput2();
				
		ProcessorBuilder.buildProcessor(new FileInputStream(USR_FILE_NAME));
		Processor processor = ProcessorBuilder.getProcessor();
		
		while(!processor.isTerminated())
			processor.runClockCycle();
		
		assertEquals(
				"The result from the last ADDI in r3 should be 5.",
				625,
				processor.getRegisterFile().getRegisterValue((byte) 5));
		
		assertEquals(
				"The result from the last ADDI in r3 should be 5.",
				111,
				processor.getTimer());
		
		TestsInitializer.clean();
	}
	
}
