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
		if(this.getQj() == 0 && this.getQk() == 0){
			//TODO go into execution with cycles, and execute in last cycle
		}
	}

	@Override
	public void writeInstruction() {
		//TODO CDB available -- calculate result
		short result = 0;
		for(ReservationStation rs: ProcessorBuilder.getProcessor().getReservationStations()){
			if(rs.getQj() == this.getDestROB()){
				rs.setVj(result);
			}
			if(rs.getQk() == this.getDestROB()){
				rs.setVk(result);
			}
		}
		ProcessorBuilder.getProcessor().getROB().getEntry(this.getDestROB()).setValue(result);
		this.clearBusy();
	}

}
