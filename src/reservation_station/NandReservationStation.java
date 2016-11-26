package reservation_station;

import units.Processor;

public class NandReservationStation extends IntOpernationReservationStation {

	protected NandReservationStation(Processor simulator) {
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
