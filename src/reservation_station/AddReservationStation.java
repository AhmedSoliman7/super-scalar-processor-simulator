package reservation_station;

import units.Processor;

public class AddReservationStation extends IntOpernationReservationStation {

	protected AddReservationStation(Processor processor, boolean isOriginal) {
		super(processor);
		if(isOriginal){
			this.setTempReservationStation(new AddReservationStation(processor, false));
		}
	}
}
