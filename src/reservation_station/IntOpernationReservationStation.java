package reservation_station;

import main.ProcessorBuilder;
import units.InstructionDecoder;
import units.InstructionType;

public abstract class IntOpernationReservationStation extends ReservationStation {

	@Override
	public void issueInstruction(short instruction, short instructionAddress, short destROB) {
		super.issueInstruction(instruction, instructionAddress, destROB);
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
			short imm = InstructionDecoder.getImmediate(instruction);
			this.setAddress(imm);
			short effectiveAddress = (short) (imm + 1 + this.getInstructionAddress());
			ProcessorBuilder.getProcessor().getROB().getEntry(destROB).setDestination(effectiveAddress);
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
		
		passToOtherReservationStations(result);
		
		byte destRegister = (byte) ProcessorBuilder.getProcessor()
				.getROB()
				.getEntry(this.getDestROB())
				.getDestination();

		ProcessorBuilder.getProcessor().setReadyRegister(destRegister);
		ProcessorBuilder.getProcessor().setReadyValue(result);
		
		this.setState(ReservationStationState.COMMIT);
		this.clearBusy();
	}

}
