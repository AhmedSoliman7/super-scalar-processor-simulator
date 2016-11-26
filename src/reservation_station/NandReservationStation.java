package reservation_station;

import units.Processor;

public class NandReservationStation extends IntOpernationReservationStation {

	protected NandReservationStation(Processor processor, boolean isOriginal) {
		super(processor);
		if(isOriginal)
			this.setTempReservationStation(new NandReservationStation(processor, false));
	}
}
