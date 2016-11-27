package test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class TestsInitializer {
	
	static final String ASM_FILE_NAME = "testFile.asm";
	static final String USR_FILE_NAME = "testFile.usr";

	static void initAssembly() throws FileNotFoundException {
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
	
	static void initAssembly2() throws FileNotFoundException {
		PrintWriter out = new PrintWriter(ASM_FILE_NAME);

		out.println("LW r6, r2, 32");
		out.println("LW r2, r3, 44");
		out.println("MULT r3, r2, r6");
		out.println("SUB r7, r3, r6");
		out.println("MULT r6, r3, r7");
		out.println("ADD r4, r7, r6");

		out.flush();
		out.close();
	}
	
	static void initGivenAssembly(ArrayList<String> instructions) throws FileNotFoundException {
		PrintWriter out = new PrintWriter(ASM_FILE_NAME);

		for(String inst: instructions) {
			out.println(inst);
		}

		out.flush();
		out.close();
	}
	
	static void initUserInput() throws FileNotFoundException {
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
	
	static void initUserInput2() throws FileNotFoundException {
		PrintWriter out = new PrintWriter(USR_FILE_NAME);

		out.println("1");
		out.println("2");
		out.println("8 2 1 0 1");
		out.println("1");
		out.println("4");
		out.println("4");
		out.println("2 1");
		out.println("2 1");
		out.println("2 2");
		out.println("2 2");
		out.println("1 2");
		out.println(ASM_FILE_NAME);
		out.println("100");
		out.println("2");
		out.println("32 2");
		out.println("44 3");

		out.flush();
		out.close();
	}
	
	static void clean() {
		File file = new File(ASM_FILE_NAME);
		file.delete();

		file = new File(USR_FILE_NAME);
		file.delete();
	}
}
