package units;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import memory.MemoryHandler;
import memory.ReturnPair;
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
	 * 1. Execute instructions
	 * 2. Write result instructions
	 * 3. Commit instructions
	 */
	private static final byte VALID = -1;
	private ReservationStation[] reservationStations;	
	private ReorderBuffer ROB;							
	private short[] registerFile;						
	private short[] registerStatus;						
	private MemoryHandler memoryUnit;					
	private Queue<Short> instructionQueue;				
	private int instructionQueueMaxSize;
	private int pipelineWidth;							
	private int[] firstReservationStation;
	private int[] countReservationStation;
	private short PC;
	private InstructionInFetch instructionInFetch;

	public Processor(){
		registerFile = new short[8];
		registerStatus = new short[8];
		Arrays.fill(registerStatus, (short) VALID);
		countReservationStation = new int[5];
		instructionQueue = new LinkedList<Short>();

		prepareReservationStations();
	}
	
	public void runClockCycle() {
		fetchInstruction();
		// TODO rest of this clock cycle
	}
	
	public void fetchInstruction() {
		if(instructionQueue.size() == instructionQueueMaxSize) {
			return;
		}
		
		if(instructionInFetch != null && !instructionInFetch.isReady()) {
			instructionInFetch.decrementCycles();
			
			return;
		}
	
		if(instructionInFetch != null) {
			instructionQueue.add(instructionInFetch.getInstruction());
		}
		
		ReturnPair<Short> instructionPair = memoryUnit.fetchInstruction(PC++);
		instructionInFetch = new InstructionInFetch(instructionPair.value, (short) (instructionPair.clockCycles - 1));
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
					reservationStations[firstReservationStation[typeIndex] + j].issueInstruction(currentInstruction, getROB().nextEntryIndex());
					instructionQueue.poll();
					continue mainLoop;
				}
			}
			break;
		}
	}

	private void executeInstructions(){
		//TODO if instruction finished issuing
		for(ReservationStation rs: reservationStations){
			rs.executeInstruction();
		}
	}

	private void writeResultInstructions(){
		for(ReservationStation rs: reservationStations){
			//TODO if rs finished execution
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
			rs.clearBusy();
		}
	}

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

	public void setPipelineWidth(int pipelineWidth) {
		this.pipelineWidth = pipelineWidth;
	}

	public void setRegisterValue(byte register, short value) {
		registerFile[register] = value;
	}

	public ReservationStation[] getReservationStations(){
		return reservationStations;
	}
	
	public void setPC(short PC) {
		this.PC = PC;
	}
	
	public InstructionInFetch getInstructionInFetch() {
		return instructionInFetch;
	}
	
	public Queue<Short> getInstructionQueue() {
		return instructionQueue;
	}
}
