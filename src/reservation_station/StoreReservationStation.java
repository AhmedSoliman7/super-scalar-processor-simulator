package reservation_station;

import units.InstructionDecoder;
import units.Processor;

public class StoreReservationStation extends ReservationStation {

	protected StoreReservationStation(Processor processor, boolean isOriginal) {
		super(processor);
		if(isOriginal)
			this.setTempReservationStation(new StoreReservationStation(processor, false));

	}

	@Override
	public void issueInstruction(short instruction, short destROB) {
		super.issueInstruction(instruction, destROB);
		super.issueInstructionSourceRegister2(InstructionDecoder.getRT(instruction));
		this.setAddress(InstructionDecoder.getImmediate(instruction));
	}

	@Override
	public void executeInstruction() {
		if(this.getQj() == 0){
			Processor.getProcessor().getROB().getEntry(destROB).setDestination((short) (Vj + address));
		}
	}

	@Override
	public void writeInstruction() {
		if(this.getQk() == 0){
			processor.getROB().getEntry(destROB).setValue(Vk);
			this.clearBusy();
		}
	}
}
