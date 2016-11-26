package reservation_station;

import main.ProcessorBuilder;
import units.InstructionDecoder;
import units.InstructionType;

public abstract class IntOpernationReservationStation extends ReservationStation {

	@Override
	public void issueInstruction(short instruction, short destROB) {
		super.issueInstruction(instruction, destROB);
		InstructionType opType = this.getOperationType();
		if(opType != InstructionType.ADDI) {
			super.issueInstructionSourceRegister2(InstructionDecoder.getRT(instruction));
		}
		else {
			this.setVk(InstructionDecoder.getImmediate(instruction));
		}
		
		if(opType != InstructionType.BEQ) {
			byte destRegister = opType == InstructionType.ADDI ? InstructionDecoder.getRT(instruction) : InstructionDecoder.getRD(instruction);
			ProcessorBuilder.getProcessor().getRegisterFile().setRegisterStatus(destRegister, destROB);
			ProcessorBuilder.getProcessor().getROB().getEntry(destROB).setDestination(destRegister);						
		}
		else {
			this.setAddress(InstructionDecoder.getImmediate(instruction));
		}
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
