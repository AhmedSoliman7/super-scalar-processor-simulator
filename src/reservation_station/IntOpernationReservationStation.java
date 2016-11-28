package reservation_station;

import main.ProcessorBuilder;
import units.InstructionDecoder;
import units.InstructionType;

public abstract class IntOpernationReservationStation extends ReservationStation {

	@Override
	public void issueInstruction(short instruction, short instructionAddress, short destROB) {
		
		super.issueInstruction(instruction, instructionAddress, destROB);
		InstructionType opType = this.getOperationType();
		if(opType == InstructionType.ADDI) {
			this.setVk(InstructionDecoder.getImmediate(instruction));
		}
		else if(opType == InstructionType.JMP) {
			this.setVk((short) (InstructionDecoder.getImmediate(instruction) + 1 + this.getInstructionAddress()));
		}
		else if(opType == InstructionType.RET || opType == InstructionType.JALR){
			this.setQk(READY);
		}
		else {
			super.issueInstructionSourceRegister2(InstructionDecoder.getRT(instruction));
		}
		
		if(opType == InstructionType.BEQ){
			short imm = InstructionDecoder.getImmediate(instruction);
			this.setAddress(imm);
			short effectiveAddress = (short) (imm + 1 + this.getInstructionAddress());
			ProcessorBuilder.getProcessor().getROB().getEntry(destROB).setDestination(effectiveAddress);
		}
		else if(opType != InstructionType.JMP && opType != InstructionType.RET)  {
			byte destRegister = opType == InstructionType.ADDI || opType == InstructionType.JALR ? InstructionDecoder.getRT(instruction) : InstructionDecoder.getRD(instruction);
			ProcessorBuilder.getProcessor().getRegisterFile().setRegisterStatus(destRegister, destROB);
			ProcessorBuilder.getProcessor().getROB().getEntry(destROB).setDestination(destRegister);						
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
		
		if(this.getOperationType() == InstructionType.JALR) {
			ProcessorBuilder.getProcessor().getROB().getEntry(this.getDestROB()).setValue((result << 16) | this.getVj());
		}
		else 
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
