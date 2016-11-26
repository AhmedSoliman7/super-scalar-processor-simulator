package reservation_station;

public class AddReservationStation extends IntOpernationReservationStation {

	protected AddReservationStation(boolean isOriginal) {
		if(isOriginal){
			this.setTempReservationStation(new AddReservationStation(false));
		}
	}
}
