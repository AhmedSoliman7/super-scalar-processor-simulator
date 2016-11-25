package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import memory.MemoryHandler;

public class Parser {

	HashMap<String, Short> instructionMap;
	HashMap<String, Short> registerMap;
	
	public Parser() throws FileNotFoundException {
		initializeInstructionMap();
		initializeRegisterMap();
	}
	
	private void initializeInstructionMap() throws FileNotFoundException {
		instructionMap = new HashMap<String, Short>();
		
		Scanner sc = new Scanner(new File("resources/instructions.csv"));
		sc.nextLine();
		
		while(sc.hasNextLine()) {
			String[] instructionSettings = sc.nextLine().split(",");
			
			String instruction = instructionSettings[0];
			short opcode = Short.parseShort(instructionSettings[1]);
			
			short instructionCode = (short) (opcode << 13);
			
			if(opcode == 0) {
				short aluOp = Short.parseShort(instructionSettings[2]);
				instructionCode |= (aluOp << 9);
			}
			
			instructionMap.put(instruction, instructionCode);
		}
		
		sc.close();
	}
	
	private void initializeRegisterMap() {
		registerMap = new HashMap<String, Short>();
		
		for(short r = 0; r < 8; r++) {
			registerMap.put("r" + r, r);
			registerMap.put("R" + r, r);
			registerMap.put("" + r, r);
		}
	}
	
	private static short getOpcode(Short instruction) {
		return (short) ((instruction >> 13) & 7);
	}
	
	private short parseInstruction(String[] assembly) {
		short code = instructionMap.get(assembly[0].toUpperCase());
		
		short opcode = getOpcode(code);
		
		if(opcode == 0) {
			short destination = registerMap.get(assembly[1]);
			short source1 = registerMap.get(assembly[2]);
			short source2 = registerMap.get(assembly[3]);
			
			code |= (destination << 6) | (source1 << 3) | source2;
		}
		else if(opcode < 5) {
			short regA = registerMap.get(assembly[1]);
			short regB = registerMap.get(assembly[2]);
			short imm = Short.parseShort(assembly[3]);
			
			code |= (regA << 10) | (regB << 7) | (imm & ((1 << 7) - 1));
		}
		else {
			short regA = registerMap.get(assembly[1]);
			code |= (regA << 10);
			
			if(opcode == 5) {
				short imm = Short.parseShort(assembly[2]);
				
				code |= ((imm & ((1 << 7) - 1)) << 3);
			}
			else if(opcode == 6) {
				short regB = registerMap.get(assembly[2]);
				code |= (regB << 7);
			}
		}
		
		return code;
	}
	
	private static String[] filterTokens(String[] in)
	{
		int i = 0, j = 0;
		while(i < in.length)
		{
			if(in[i] != null && !in[i].trim().isEmpty())
				in[j++] = in[i];
			++i;
		}
		return Arrays.copyOf(in, j);
	}
	
	public void readProgram(String filePath, short initialAddress, MemoryHandler memoryHandler) throws FileNotFoundException {
		Scanner sc = new Scanner(new File(filePath));
		
		while(sc.hasNextLine()) {
			String[] assembly = filterTokens(sc.nextLine().trim().split(" |,"));
			short instructionCode = parseInstruction(assembly);
			
			memoryHandler.getMainMemory().write(initialAddress++, instructionCode);
		}
		
		sc.close();
	}
}
