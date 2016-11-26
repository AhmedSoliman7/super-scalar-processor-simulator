package reservation_station;

import units.InstructionDecoder;
import units.Processor;

public class IntOpernationReservationStation extends ReservationStation {

	protected IntOpernationReservationStation(Processor processor) {
		super(processor);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void issueInstruction(short instruction, short destROB) {
		super.issueInstruction(instruction, destROB);
		super.issueInstructionSourceRegister2(InstructionDecoder.getRT(instruction));
		byte rd = InstructionDecoder.getRD(instruction);
		processor.setRegisterStatus(rd, destROB);
		processor.getROB().getEntry(destROB).setDestination(rd);
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
