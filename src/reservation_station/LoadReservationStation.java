package reservation_station;

import main.ProcessorBuilder;
import memory.ReturnPair;
import units.InstructionDecoder;

public class LoadReservationStation extends ReservationStation {
	private static int cycles;
	private ReturnPair<Short> fetchedPair;

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
		ProcessorBuilder.getProcessor().getRegisterFile().setRegisterStatus(rt, destROB);
		ProcessorBuilder.getProcessor().getROB().getEntry(destROB).setDestination(rt);
	}

	@Override
	public void executeInstruction() {

		short newAddress = (short) (this.getVj() + this.getAddress());
		if(this.getTimerTillNextState() == 0 && this.getQj() == READY && !ProcessorBuilder.getProcessor().getROB().findMatchingStoreAddress(newAddress, this.getDestROB())){
			this.setAddress(newAddress);

			ReturnPair<Short> readPair = ProcessorBuilder.getProcessor()
					.getMemoryUnit()
					.read(newAddress);

			this.fetchedPair = readPair;
		}
		
		if(this.fetchedPair == null) {
			return;
		}
		
		this.incrementTimer();

		if(readyToWrite()) {
			this.setState(ReservationStationState.WRITE);
		}
	}

	@Override
	public void writeInstruction() {
		ProcessorBuilder.getProcessor()
		.getROB()
		.getEntry(this.getDestROB())
		.setValue(fetchedPair.value);

		passToOtherReservationStations(fetchedPair.value);

		byte destRegister = (byte) ProcessorBuilder.getProcessor()
				.getROB()
				.getEntry(this.getDestROB())
				.getDestination();

		ProcessorBuilder.getProcessor().setReadyRegister(destRegister);
		ProcessorBuilder.getProcessor().setReadyValue(this.fetchedPair.value);

		this.setState(ReservationStationState.COMMIT);
		this.clearBusy();
	}

	@Override
	boolean readyToWrite() {
		return this.getTimerTillNextState() == LoadReservationStation.cycles + this.fetchedPair.clockCycles;
	}
}
