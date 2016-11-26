package reservation_station;

public class NandReservationStation extends IntOpernationReservationStation {
	private static int cycles;
	
	protected NandReservationStation(boolean isOriginal) {
		if(isOriginal)
			this.setTempReservationStation(new NandReservationStation(false));
	}
	
	public static int getCycles() {
		return cycles;
	}

	public static void setCycles(int cycles) {
		NandReservationStation.cycles = cycles;
	}
}
