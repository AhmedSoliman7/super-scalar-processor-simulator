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
		if(Qj == 0 && !processor.getROB().findMatchingStoreAddress((short) (address + Vj), destROB)){
			address += Vj;
			//TODO: read from memory
			clear();
		}
		
	}

	@Override
	public void writeInstruction() {
		// TODO CDB available
		
	}

}
