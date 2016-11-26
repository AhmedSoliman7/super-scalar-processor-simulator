package reservation_station;

public class MultReservationStation extends IntOpernationReservationStation {

	protected MultReservationStation(boolean isOriginal) {
		if(isOriginal)
			this.setTempReservationStation(new MultReservationStation(false));
	}
}
