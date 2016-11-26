package reservation_station;

import main.ProcessorBuilder;
import units.InstructionDecoder;
import units.Processor;

public class LoadReservationStation extends ReservationStation {
	private static int cycles;
	
	protected LoadReservationStation(boolean isOriginal) {
		if(isOriginal)
			this.setTempReservationStation(new LoadReservationStation(false));
	}
	
	public static int getCycles() {
		return cycles;
	}

	public static void setCycles(int cycles) {
		LoadReservationStation.cycles = cycles;
	}

	@Override
	public void issueInstruction(short instruction, short destROB) {
		super.issueInstruction(instruction, destROB);
		this.setAddress(InstructionDecoder.getImmediate(instruction));
		byte rt = InstructionDecoder.getRT(instruction);
		ProcessorBuilder.getProcessor().setRegisterStatus(rt, destROB);
		ProcessorBuilder.getProcessor().getROB().getEntry(destROB).setDestination(rt);
	}

	@Override
	public void executeInstruction() {
		short newAddress = (short) (this.getVj() + this.getAddress());
		if(this.getQj() == 0 && !ProcessorBuilder.getProcessor().getROB().findMatchingStoreAddress(newAddress, this.getDestROB())){
			this.setAddress(newAddress);
			//TODO: read from memory
			this.clearBusy();
		}
		
	}

	@Override
	public void writeInstruction() {
		// TODO CDB available
		
	}

}
