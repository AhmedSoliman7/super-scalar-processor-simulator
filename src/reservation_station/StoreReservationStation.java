package reservation_station;

import main.ProcessorBuilder;
import units.InstructionDecoder;

public class StoreReservationStation extends ReservationStation {

	protected StoreReservationStation(boolean isOriginal) {
		if(isOriginal)
			this.setTempReservationStation(new StoreReservationStation(false));

	}

	@Override
	public void issueInstruction(short instruction, short destROB) {
		super.issueInstruction(instruction, destROB);
		super.issueInstructionSourceRegister2(InstructionDecoder.getRT(instruction));
		this.setAddress(InstructionDecoder.getImmediate(instruction));
	}

	@Override
	public void executeInstruction() {
		short newAddress = (short) (this.getVj() + this.getAddress());
		if(this.getQj() == 0){
			ProcessorBuilder.getProcessor().getROB().getEntry(this.getDestROB()).setDestination(newAddress);
		}
	}

	@Override
	public void writeInstruction() {
		if(this.getQk() == 0){
			ProcessorBuilder.getProcessor().getROB().getEntry(this.getDestROB()).setValue(this.getVk());
			this.clearBusy();
		}
	}
}
