package reservation_station;

public class MultReservationStation extends IntOpernationReservationStation {
	private static int cycles;
	
	protected MultReservationStation(boolean isOriginal) {
		if(isOriginal)
			this.setTempReservationStation(new MultReservationStation(false));
	}
	
	public static int getCycles() {
		return cycles;
	}

	public static void setCycles(int cycles) {
		MultReservationStation.cycles = cycles;
	}
}
