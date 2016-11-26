package reservation_station;

public class NandReservationStation extends IntOpernationReservationStation {

	protected NandReservationStation(boolean isOriginal) {
		if(isOriginal)
			this.setTempReservationStation(new NandReservationStation(false));
	}
}
