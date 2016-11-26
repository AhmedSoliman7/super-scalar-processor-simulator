package test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.junit.Test;

import main.ProcessorBuilder;

public class ProcessorTests {

	@Test
	public void testInstructionFetch() throws Exception {
		initAssembly();
		initUserInput();

		ProcessorBuilder builder = new ProcessorBuilder();
		builder.buildProcessor(new FileInputStream(USR_FILE_NAME));
		
		builder.getProcessor().fetchInstruction();
		
		for(int i = 0; i < 56; i++)
			builder.getProcessor().getInstructionInFetch().decrementCycles();
		
		assertEquals(
				"Instruction should be ready after 56 clock cycles.",
				true,
				builder.getProcessor().getInstructionInFetch().isReady());
		
		assertEquals(
				"Instruction queue should be still empty.",
				0,
				builder.getProcessor().getInstructionQueue().size());
		
		builder.getProcessor().fetchInstruction();
		
		assertEquals(
				"Instruction queue should have one fetched instruction.",
				1,
				builder.getProcessor().getInstructionQueue().size());
		
		builder.getProcessor().fetchInstruction();
		
		assertEquals(
				"Instruction queue should have two fetched instructions because the second one was cached in L1.",
				2,
				builder.getProcessor().getInstructionQueue().size());
		
		clean();
	}
	
	static final String ASM_FILE_NAME = "testFile.asm";
	static final String USR_FILE_NAME = "testFile.usr";
	
	void initAssembly() throws FileNotFoundException {
		PrintWriter out = new PrintWriter(ASM_FILE_NAME);

		out.println("ADD r1,r2 r7");
		out.println("SuB R7, r6 4");
		out.println("NAND r1, r5, r3");
		out.println("MULT r7, r7, r7");
		out.println("LW r6, r7, 32");
		out.println("SW r1, r6, -16");
		out.println("ADDI r2, r1, 15");
		out.println("beq r7, r5, 4");
		out.println("jmp r2, 13");
		out.println("JALR r1, r2");
		out.println("RET r1");

		out.flush();
		out.close();
	}

	void initUserInput() throws FileNotFoundException {
		PrintWriter out = new PrintWriter(USR_FILE_NAME);

		out.println("3");
		out.println("10");
		out.println("4 2 1 0 1");
		out.println("8 2 2 0 3");
		out.println("16 4 4 0 5");
		out.println("2");
		out.println("5");
		out.println("5");
		out.println("1 2");
		out.println("1 2");
		out.println("1 2");
		out.println("1 2");
		out.println("1 2");
		out.println(ASM_FILE_NAME);
		out.println("100");
		out.println("20");
		for(int i = 0; i < 20; i++)
			out.printf("%d %d\n", i, i + 1);

		out.flush();
		out.close();
	}

	void clean() {
		File file = new File(ASM_FILE_NAME);
		file.delete();

		file = new File(USR_FILE_NAME);
		file.delete();
	}
}
