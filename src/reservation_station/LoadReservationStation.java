package reservation_station;

import units.InstructionDecoder;
import units.Processor;

public class LoadReservationStation extends ReservationStation {

	protected LoadReservationStation(Processor processor) {
		super(processor);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void issueInstruction(short instruction, short destROB) {
		super.issueInstruction(instruction, destROB);
		address = InstructionDecoder.getImmediate(instruction);
		byte rt = InstructionDecoder.getRT(instruction);
		processor.setRegisterStatus(rt, destROB);
		processor.getROB().getEntry(destROB).setDestination(rt);
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
