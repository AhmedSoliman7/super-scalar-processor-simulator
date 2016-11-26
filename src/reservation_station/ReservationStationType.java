package reservation_station;

public enum ReservationStationType {

	LOAD(0), STORE(1), ADD(2), MULT(3), NAND(4);
	
	private final int index;
	
	private ReservationStationType(int val) {
		index = val;
	} 	
	
	public int getValue(){
		return index;
	}
	
//	public static ReservationStationType getTypeIndex(short instruction) {
//		short opcode = (short) ((instruction >> 13) & 7);
//		if(opcode == 0){
//			
//		}
//	}
	
	
}
