package units;

public enum InstructionType {

	ADD, SUB, NAND, MULT, LOAD, STORE, ADDI, BEQ, JMP, JALR, RET;

	public static InstructionType getInstructionType(short instruction) {
		short opcode = InstructionDecoder.getOpcode(instruction);
		if(opcode == 0) {
			short regOp = InstructionDecoder.getRegOp(instruction);
			return InstructionType.values()[regOp];
		}
		return InstructionType.values()[3 + opcode];
	}
}
