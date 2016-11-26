package reservation_station;

import units.Processor;

public class AddReservationStation extends IntOpernationReservationStation {

	protected AddReservationStation(Processor processor) {
		super(processor);
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
