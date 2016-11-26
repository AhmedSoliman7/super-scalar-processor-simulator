package reservation_station;

import units.InstructionDecoder;
import units.Processor;
import units.ReorderBufferEntry;

public abstract class ReservationStation {

	private static final int VALID = -1;
	private Processor processor;
	private byte opType;
	private short Qj, Qk, Vj, Vk, destROB, address;
	private boolean busy;
	private ReservationStation tempRS;
	
	protected ReservationStation(Processor processor){
		this.processor = processor;
	}
	
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
		setBusy();
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
	
	public boolean isBusy() {
		return busy;
	}
	
	public void setBusy() { 
		busy = true;
	}
	
	public void clearBusy() {
		busy = false;
	}
	
	public void setVj(short value){
		Vj = value;
		Qj = 0;
	}
	
	public void setVk(short value){
		Vk = value;
		Qk = 0;
	}
	
	public short getQj(){
		return Qj;
	}
	
	public short getQk(){
		return Qk;
	}
	
	public void setTempReservationStation(ReservationStation rs) {
		tempRS = rs;
	}
}
