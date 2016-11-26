package reservation_station;

import main.ProcessorBuilder;
import units.InstructionDecoder;

public abstract class IntOpernationReservationStation extends ReservationStation {

	@Override
	public void issueInstruction(short instruction, short destROB) {
		super.issueInstruction(instruction, destROB);
		super.issueInstructionSourceRegister2(InstructionDecoder.getRT(instruction));
		byte rd = InstructionDecoder.getRD(instruction);
		ProcessorBuilder.getProcessor().getRegisterFile().setRegisterStatus(rd, destROB);
		ProcessorBuilder.getProcessor().getROB().getEntry(destROB).setDestination(rd);
	}

	@Override
	public void executeInstruction() {
		if(this.getQj() == READY && this.getQk() == READY){
			this.incrementTimer();
		}
		
		if(readyToWrite()) {
			this.setState(ReservationStationState.WRITE);
		}
	}
	
	abstract short calculate();
	
	@Override
	public void writeInstruction() {
		short result = calculate();
		passToOtherReservationStations(result);
		ProcessorBuilder.getProcessor().getROB().getEntry(this.getDestROB()).setValue(result);
		
		this.setState(ReservationStationState.COMMIT);
		this.clearBusy();
	}

}
