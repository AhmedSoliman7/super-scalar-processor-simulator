package reservation_station;

import units.InstructionType;

public class AddReservationStation extends IntOpernationReservationStation {
	private static int cycles;
	
	protected AddReservationStation(boolean isOriginal) {
		if(isOriginal){
			this.setTempReservationStation(new AddReservationStation(false));
		}
	}
	
	@Override
	short calculate() {
		InstructionType type = this.getOperationType();
		switch (type) {
		case ADD: case ADDI:
			return (short) (this.getVj() + this.getVk());
		case SUB: case BEQ:
			return (short) (this.getVj() - this.getVk());
		default:
			throw new RuntimeException("Error! Unknown operation type in ADD reservation station.");
		}
	}

	@Override
	boolean readyToWrite() {
		return this.getTimerTillNextState() == AddReservationStation.cycles;
	}
	
	public static void setCycles(int cycles) {
		AddReservationStation.cycles = cycles;
	}
}
