package reservation_station;

import units.InstructionDecoder;

public enum ReservationStationType {

	LOAD(0), STORE(1), ADD(2), MULT(3), NAND(4);
	
	private final int index;
	
	private ReservationStationType(int val) {
		index = val;
	} 	
	
	public int getValue(){
		return index;
	}
	
	public static ReservationStationType getType(short instruction){		//TODO functional unit (type) of JMP
		
		byte opcode = InstructionDecoder.getOpcode(instruction);
		switch (opcode) {
		case 0:
			byte regOp = InstructionDecoder.getRegOp(instruction);
			if(regOp == 2){
				return NAND;
			}
			if(regOp == 3){
				return MULT;
			}
			return ADD;
		case 1:
			return LOAD;
		case 2:
			return STORE;
		default:
			return ADD;
		}
		
	}
}
