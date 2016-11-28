package test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.junit.Test;

import main.Parser;
import memory.MemoryHandler;

public class ParserTests {
	static final String FILE_NAME = "testFile.asm";

	Parser parser;

	@Test
	public void testAssemblyParsing() throws FileNotFoundException {
		MemoryHandler handler = new MemoryHandler(1, (short) 10);
		short startAddress = 10;
		initAssembly();

		parser.readProgram(FILE_NAME, startAddress, handler);
		assertEquals(
				(short) 87,
				handler.getMainMemory().getData()[startAddress++]);
		
		assertEquals(
				(short) 1012,
				handler.getMainMemory().getData()[startAddress++]);
		
		assertEquals(
				(short) 1131,
				handler.getMainMemory().getData()[startAddress++]);
		
		assertEquals(
				(short) 2047,
				handler.getMainMemory().getData()[startAddress++]);
		
		assertEquals(
				(short) 16160,
				handler.getMainMemory().getData()[startAddress++]);
		
		assertEquals(
				(short) 22768,
				handler.getMainMemory().getData()[startAddress++]);
		
		assertEquals(
				(short) 25871,
				handler.getMainMemory().getData()[startAddress++]);
		
		assertEquals(
				(short) -26748,
				handler.getMainMemory().getData()[startAddress++]);
		
		assertEquals(
				(short) -22515,
				handler.getMainMemory().getData()[startAddress++]);
		
		assertEquals(
				(short) -15104,
				handler.getMainMemory().getData()[startAddress++]);
		
		assertEquals(
				(short) -7168,
				handler.getMainMemory().getData()[startAddress++]);
		
		cleanAssembly();
	}

	void initAssembly() throws FileNotFoundException {
		parser = new Parser();
		PrintWriter out = new PrintWriter(FILE_NAME);

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

	void cleanAssembly() {
		File file = new File(FILE_NAME);
		file.delete();
	}
}
