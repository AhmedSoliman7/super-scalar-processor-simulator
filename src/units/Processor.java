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
	 * 
	 * Missing
	 * =======
	 * 1. Initialization of constructor
	 * 2. Execute instructions
	 * 2. Write result instructions
	 * 3. Commit instructions
	 */
	private static final int VALID = -1;
	private ReservationStation[] reservationStations;	
	private ReorderBuffer ROB;							//TODO initialize with size from input
	private short[] registerFile;						
	private short[] registerStatus;						
	private MemoryHandler memoryUnit;					
	private Queue<Short> instructionQueue;				//TODO initialize with size from input
	private int instructionQueueMaxSize;
	private int pipelineWidth;							//TODO input
	private int[] firstReservationStation;
	private int[] countReservationStation; 				//TODO initialize with size 5 and values from input

	public Processor(){
		//TODO: initialization
		registerFile = new short[8];
		registerStatus = new short[8];
		Arrays.fill(registerStatus, (short) VALID);
		countReservationStation = new int[5];
		
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
		mainLoop: for(int i = 0; i < pipelineWidth && !instructionQueue.isEmpty() && !getROB().isFull(); ++i){
			short currentInstruction = instructionQueue.peek();
			ReservationStationType currentType = ReservationStationType.getType(currentInstruction);
			for(int typeIndex = currentType.getValue(), j = 0; j < countReservationStation[typeIndex]; ++j){
				if(!reservationStations[firstReservationStation[typeIndex] + j].isBusy()){
					reservationStations[firstReservationStation[typeIndex] + j].issue(currentInstruction, getROB().getNextEntryIndex());
					instructionQueue.poll();
					continue mainLoop;
				}
			}
			break;
		}
	}
	
	private void executeInstructions(){
		//TODO loop on reservation stations and execute possible ones. 
	}
	
	private void writeResultInstructions(){
		//TODO loop on reservation stations and write result of possible ones
	}
	
	private void commitInstructions(){
		//TODO commit head of ROB if possible
	}
	
	/*
	 * Getters and Setters
	 * ===================
	 */
	public short getRegisterStatus(byte register) {
		return registerStatus[register];
	}

	public void setRegisterStatus(byte register, short value) {
		this.registerStatus[register] = value;
	}

	public ReorderBuffer getROB() {
		return ROB;
	}
	
	public short getRegisterValue(byte register) {
		return registerFile[register];
	}
	
	public MemoryHandler getMemoryUnit() {
		return this.memoryUnit;
	}
	
	public void setMemoryUnit(MemoryHandler memoryUnit) {
		this.memoryUnit = memoryUnit;
	}
	
	public int[] getCountReservationStation() {
		return countReservationStation;
	}

	public void setROB(ReorderBuffer rOB) {
		ROB = rOB;
	}

	public void setInstructionQueueMaxSize(int instructionQueueMaxSize) {
		this.instructionQueueMaxSize = instructionQueueMaxSize;
	}

	public void setPipelineWidth(int piplineWidth) {
		this.pipelineWidth = piplineWidth;
	}
}
