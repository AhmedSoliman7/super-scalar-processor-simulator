package reservation_station;

import units.Processor;

public class MultReservationStation extends IntOpernationReservationStation {

	protected MultReservationStation(Processor processor, boolean isOriginal) {
		super(processor);
		if(isOriginal)
			this.setTempReservationStation(new MultReservationStation(processor, false));
	}
}
