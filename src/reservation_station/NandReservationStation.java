package reservation_station;

public class NandReservationStation extends IntOpernationReservationStation {
	private static int cycles;
	
	protected NandReservationStation(boolean isOriginal) {
		if(isOriginal)
			this.setTempReservationStation(new NandReservationStation(false));
	}
	
	@Override
	short calculate() {
		return (short) ~(this.getVj() & this.getVk());
	}
	
	public static void setCycles(int cycles) {
		NandReservationStation.cycles = cycles;
	}
	
	@Override
	boolean readyToWrite() {
		return this.getTimerTillNextState() == NandReservationStation.cycles;
	}
}
