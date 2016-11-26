package reservation_station;

public class MultReservationStation extends IntOpernationReservationStation {
	private static int cycles;
	
	protected MultReservationStation(boolean isOriginal) {
		if(isOriginal)
			this.setTempReservationStation(new MultReservationStation(false));
	}
	
	@Override
	short calculate() {
		return (short) (this.getVj() * this.getVk());
	}

	public static void setCycles(int cycles) {
		MultReservationStation.cycles = cycles;
	}
	
	@Override
	boolean readyToWrite() {
		return this.getTimerTillNextState() == MultReservationStation.cycles;
	}
	
}
