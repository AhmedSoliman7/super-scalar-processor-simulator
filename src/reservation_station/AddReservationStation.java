package reservation_station;

public class AddReservationStation extends IntOpernationReservationStation {
	private static int cycles;
	
	protected AddReservationStation(boolean isOriginal) {
		if(isOriginal){
			this.setTempReservationStation(new AddReservationStation(false));
		}
	}

	public static int getCycles() {
		return cycles;
	}

	public static void setCycles(int cycles) {
		AddReservationStation.cycles = cycles;
	}
}
