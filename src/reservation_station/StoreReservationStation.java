package reservation_station;

import units.InstructionDecoder;
import units.Processor;

public class StoreReservationStation extends ReservationStation {

	protected StoreReservationStation(Processor processor) {
		super(processor);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void issueInstruction(short instruction, short destROB) {
		super.issueInstruction(instruction, destROB);
		super.issueInstructionSourceRegister2(InstructionDecoder.getRT(instruction));
		address = InstructionDecoder.getImmediate(instruction);
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
