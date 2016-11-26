package reservation_station;

import units.InstructionDecoder;
import units.Processor;
import units.ReorderBufferEntry;

public abstract class ReservationStation {

	static final int VALID = -1;
	protected Processor processor;
	protected byte opType;
	protected short Qj, Qk, Vj, Vk, destROB, address;
	protected boolean busy;
	
	/**
	 * Constructor. Called by subclasses
	 * @param processor
	 */
	protected ReservationStation(Processor processor){
		this.processor = processor;
	}
	
	/**
	 * Creates a reservation station according to the specified type
	 * @param reservationStationType
	 * @param processor
	 * @return
	 */
	public static ReservationStation create(ReservationStationType reservationStationType, Processor processor) { 
		switch(reservationStationType) {
		case LOAD : return new LoadReservationStation(processor);
		case STORE: return new StoreReservationStation(processor);
		case ADD  : return new AddReservationStation(processor);
		case MULT : return new MultReservationStation(processor);
		case NAND : return new NandReservationStation(processor);

		default : throw new RuntimeException("Not a valid reservation station type!");
		}
	}

	public void issueInstruction(short instruction, short destROB){
		issueInstructionSourceRegister1(InstructionDecoder.getRS(instruction));
		this.busy = true;
		this.destROB = destROB;
		processor.getROB().getEntry(destROB).setType(InstructionDecoder.getOpcode(instruction));
		processor.getROB().getEntry(destROB).setReady(false);
	}

	public void issueInstructionSourceRegister1(byte rs){

		if(processor.getRegisterStatus(rs) != VALID){
			ReorderBufferEntry ROBEntry = processor.getROB().getEntry(processor.getRegisterStatus(rs));
			if(ROBEntry.isReady()){
				this.Vj = ROBEntry.getValue();
				this.Qj = 0;
			}
			else{
				this.Qj = processor.getRegisterStatus(rs);
			}
		}
		else{
			this.Vj = processor.getRegisterValue(rs);
			this.Qj = 0;
		}
	}

	public void issueInstructionSourceRegister2(byte rt){

		if(processor.getRegisterStatus(rt) != VALID){
			ReorderBufferEntry ROBEntry = processor.getROB().getEntry(processor.getRegisterStatus(rt));
			if(ROBEntry.isReady()){
				this.Vk = ROBEntry.getValue();
				this.Qk = 0;
			}
			else{
				this.Qk = processor.getRegisterStatus(rt);
			}
		}
		else{
			this.Vk = processor.getRegisterValue(rt);
			this.Qk = 0;
		}
	}

	public abstract void executeInstruction();

	public abstract void writeInstruction();
}
