package reservation_station;

public class AddReservationStation extends IntOpernationReservationStation {
	private static int cycles;
	
	protected AddReservationStation(boolean isOriginal) {
		if(isOriginal){
			this.setTempReservationStation(new AddReservationStation(false));
		}
	}
	
	
	@Override
	short calculate() {
		return (short) (this.getVj() + this.getVk());
	}

	@Override
	boolean readyToWrite() {
		return this.getTimerTillNextState() == AddReservationStation.cycles;
	}
	
	public static void setCycles(int cycles) {
		AddReservationStation.cycles = cycles;
	}
}
