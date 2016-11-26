package units;

import java.util.LinkedList;
import java.util.Queue;

import memory.MemoryHandler;
import memory.ReturnPair;
import reservation_station.ReservationStation;
import reservation_station.ReservationStationState;
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
	private ReservationStation[] reservationStations;	
	private ReorderBuffer ROB;				
	private MemoryHandler memoryUnit;
	private RegisterFile registerFile;
	private Queue<Short> instructionQueue;				
	private int instructionQueueMaxSize;
	private int pipelineWidth;							
	private int[] firstReservationStation;
	private int[] countReservationStation;
	private short PC;
	private InstructionInFetch instructionInFetch;

	public Processor(){
		countReservationStation = new int[5];
		registerFile = new RegisterFile(8, true);
		instructionQueue = new LinkedList<Short>();
		prepareReservationStations();
	}
	
	public void runClockCycle() {
		writeResultInstructions();
		executeInstructions();
		issueInstructions();
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
				reservationStations[firstReservationStation[i] + j] = ReservationStation.create(ReservationStationType.values()[i]);
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
		for(ReservationStation rs: reservationStations){
			if(rs.isBusy() && rs.getState() == ReservationStationState.EXEC) {
				rs.executeInstruction();
			}
		}
	}

	private void writeResultInstructions(){
		for(ReservationStation rs: reservationStations){
			if(rs.isBusy() && rs.getState() == ReservationStationState.WRITE) {
				rs.writeInstruction();
			}
		}
	}

	private void commitInstructions(){
		ROB.commit();
	}

	public void clear() {
		registerFile.clearStatus();
		
		ROB.clear();
		for(ReservationStation rs: reservationStations){
			rs.clearBusy();
		}
	}
	
	private void flush() {
		registerFile.flush();
		ROB.flush();
		for(ReservationStation rs: reservationStations)
			rs.flush();
	}
	
	public ReorderBuffer getROB() {
		return ROB;
	}
	
	public RegisterFile getRegisterFile() {
		return registerFile;
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
