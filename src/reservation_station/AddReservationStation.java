package reservation_station;

import javax.swing.plaf.synth.SynthSpinnerUI;

import main.ProcessorBuilder;
import units.InstructionType;

public class AddReservationStation extends IntOpernationReservationStation {
	private static int cycles;
	
	protected AddReservationStation(boolean isOriginal) {
		if(isOriginal){
			this.setTempReservationStation(new AddReservationStation(false));
		}
	}
	
	@Override
	short calculate() {
		InstructionType type = this.getOperationType();
		switch (type) {
		case ADD: case ADDI:
			return (short) (this.getVj() + this.getVk());
		case SUB:
			return (short) (this.getVj() - this.getVk());
		case BEQ:
			short diff = (short) (this.getVj() - this.getVk());
			return predictionResult(diff, this.getAddress());
		case JMP:
			ProcessorBuilder.getProcessor().getROB().getEntry(this.getDestROB()).setDestination((short) (this.getVj() + this.getVk()));
			return 0;
		case RET:
			ProcessorBuilder.getProcessor().getROB().getEntry(this.getDestROB()).setDestination(this.getVj());
			return 0;
		case JALR:
		default:
			return (short) (this.getInstructionAddress() + 1);
		}
	}

	@Override
	boolean readyToWrite() {
		return this.getTimerTillNextState() == AddReservationStation.cycles;
	}
	
	public static void setCycles(int cycles) {
		AddReservationStation.cycles = cycles;
	}
	
	private short predictionResult(short diff, short address) {
		if(diff == 0 && address >= 0 || diff != 0 && address < 0)
			return 1;												//correct branch prediction					
		if(address >= 0) {
			ProcessorBuilder.getProcessor().getROB().getEntry(this.getDestROB()).setDestination((short) (this.getInstructionAddress() + 1));
		}
		return 0;
	}
}
