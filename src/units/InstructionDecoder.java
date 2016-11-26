package units;

public class InstructionDecoder {

	public static byte getRS(short instruction){
		short opcode = getOpcode(instruction);
		if(opcode == 0)
			return (byte) (instruction >> 3 & 7);
		return (byte) (instruction >> 10 & 7);
	}
	
	public static byte getRT(short instruction){
		short opcode = getOpcode(instruction);
		if(opcode == 0)
			return (byte) (instruction & 7);
		return (byte) (instruction >> 7 & 7);
	}
	
	public static byte getRD(short instruction){
		return (byte) (instruction >> 6 & 7);
	}
	
	
	public static byte getImmediate(short instruction){
		return (byte) (instruction & 127);
	}
	
	public static byte getOpcode(short instruction){
		return (byte) (instruction >> 13 & 7);
	}
	
	public static byte getRegOp(short instruction){
		return (byte) (instruction >> 9 & 15);
	}
}
