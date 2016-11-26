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
		if(Qj == 0 && Qk == 0){
			//TODO go into excution with cycles, and execute in last cycle
		}
	}

	@Override
	public void writeInstruction() {
		//TODO CDB available -- calculate result
		short result;
		for(ReservationStation rs: processor.getReservationStations()){
			if(rs.getQj() == destROB){
				rs.setVj(result);
			}
			if(rs.getQk() == destROB){
				rs.setVk(result);
			}
		}
		processor.getROB().getEntry(destROB).setValue(result);
		this.clearBusy();
	}

}
