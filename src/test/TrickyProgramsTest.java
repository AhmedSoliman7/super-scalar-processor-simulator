package test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import main.ProcessorBuilder;
import units.Processor;

public class TrickyProgramsTest {
	
	static final String USR_FILE_NAME = "testFile.usr";
	
	@BeforeClass
    public static void init() throws FileNotFoundException {
        System.setOut(new PrintStream("test.out"));
    }
	
	@AfterClass
	public static void clean() {
		TestsInitializer.clean();
		System.setOut(System.out);
        File f = new File("test.out");
        f.delete();
    }
	
	@Test
	public void TestLoop() throws FileNotFoundException {
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
		
		
		for(int i = 0; i < 127; i++)
			processor.runClockCycle();
		
		assertEquals(
				"The result from the last ADDI in r3 should be 5.",
				625,
				processor.getRegisterFile().getRegisterValue((byte) 5));
	}
	
	@Test
	public void TestRecursion() throws FileNotFoundException {
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
		TestsInitializer.initUserInput2();
				
		ProcessorBuilder.buildProcessor(new FileInputStream(USR_FILE_NAME));
		Processor processor = ProcessorBuilder.getProcessor();
		
		for(int i = 0; i < 155; i++)
			processor.runClockCycle();
		
		assertEquals(
				"The result from the last ADDI in r3 should be 5.",
				16,
				processor.getRegisterFile().getRegisterValue((byte) 5));
	}
}
