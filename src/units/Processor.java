package units;

import java.util.Arrays;
import java.util.Queue;

import memory.MemoryHandler;
import reservation_station.ReservationStation;
import reservation_station.ReservationStationType;

public class Processor {

	/*
	 * TODO
	 * fetch, issue, write, commit => one cycle each. Execute N cycles
	 * Unconditional => predicated as taken
	 * Conditional => taken for +ve offset and not taken otherwise
	 * Handle simultaenously running instructions
	 * 
	 * Missing
	 * =======
	 * 1. Initialization of constructor
	 * 2. Execute instructions
	 * 2. Write result instructions
	 * 3. Commit instructions
	 */
	private static final byte VALID = -1;
	private ReservationStation[] reservationStations;	
	private ReorderBuffer ROB;							//TODO initialize with size from input
	private short[] registerFile;						//TODO initialize with size 8
	private short[] registerStatus;						//TODO initialize with size 8 and fill with VALID
	private MemoryHandler memoryUnit;					//TODO input
	private Queue<Short> instructionQueue;				//TODO initialize with size from input
	private int piplineWidth;							//TODO input
	private int[] firstReservationStation;
	private int[] countReservationStation; 				//TODO initialize with size 5 and values from input
	
	public Processor(){
		//TODO: initialization
		
		
		
		
		
		 prepareReservationStations();
	}
	
	private void prepareReservationStations(){
		int totalRS = 0;
		firstReservationStation = new int[5];
		for(int i = 0; i < 5; i++){
			firstReservationStation[i] = totalRS;
			totalRS += countReservationStation[i];
		}
		reservationStations = new ReservationStation[totalRS];
		for(int i = 0; i < 5; i++){
			for(int j = 0; j < countReservationStation[i]; j++){
				reservationStations[firstReservationStation[i] + j] = ReservationStation.create(ReservationStationType.values()[i], this);
			}
		}	
	}
	
	private void issueInstructions(){
		mainLoop: for(int i = 0; i < piplineWidth && !instructionQueue.isEmpty() && !getROB().isFull(); ++i){
			short currentInstruction = instructionQueue.peek();
			ReservationStationType currentType = ReservationStationType.getType(currentInstruction);
			for(int typeIndex = currentType.getValue(), j = 0; j < countReservationStation[typeIndex]; ++j){
				if(!reservationStations[firstReservationStation[typeIndex] + j].isBusy()){
					reservationStations[firstReservationStation[typeIndex] + j].issueInstruction(currentInstruction, getROB().getNextEntryIndex());
					instructionQueue.poll();
					continue mainLoop;
				}
			}
			break;
		}
	}
	
	private void executeInstructions(){
		for(ReservationStation rs: reservationStations){
			rs.executeInstruction();
		}
	}
	
	private void writeResultInstructions(){
		for(ReservationStation rs: reservationStations){
			rs.writeInstruction();
		}
	}
	
	private void commitInstructions(){
		ROB.commit();
	}
	
	public void clear() {
		Arrays.fill(registerStatus, VALID);
		ROB.clear();
		for(ReservationStation rs: reservationStations){
			rs.clear();
		}
	}
	
	/*
	 * Getters and Setters
	 * ===================
	 */
	public short getRegisterStatus(byte register) {
		return registerStatus[register];
	}

	public void setRegisterStatus(byte register, short value) {
		registerStatus[register] = value;
	}

	public ReorderBuffer getROB() {
		return ROB;
	}
	
	public short getRegisterValue(byte register) {
		return registerFile[register];
	}
	
	public void setRegisterValue(byte register, short value) {
		registerFile[register] = value;
	}
	
	public ReservationStation[] getReservationStations(){
		return reservationStations;
	}
	
	public MemoryHandler getMemoryUnit(){
		return memoryUnit;
	}
}
