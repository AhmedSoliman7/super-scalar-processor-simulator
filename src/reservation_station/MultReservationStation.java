package reservation_station;

import units.Processor;

public class MultReservationStation extends IntOpernationReservationStation {

	protected MultReservationStation(Processor simulator) {
		super(simulator);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void issueInstruction(short instruction, short destROB) {
		super.issueInstruction(instruction, destROB);
		
	}

	@Override
	public void executeInstruction() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeInstruction() {
		// TODO Auto-generated method stub
		
	}

}
