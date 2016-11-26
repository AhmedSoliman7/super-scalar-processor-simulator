package reservation_station;

import main.ProcessorBuilder;
import units.InstructionDecoder;
import units.Processor;
import units.ReorderBufferEntry;

public abstract class ReservationStation {

	private static final int VALID = -1;
	private byte opType;
	private short Qj, Qk, Vj, Vk, destROB, address;
	private boolean busy;
	private ReservationStation tempRS;
	
	public static ReservationStation create(ReservationStationType reservationStationType, Processor processor) { 
		switch(reservationStationType) {
		case LOAD : return new LoadReservationStation(true);
		case STORE: return new StoreReservationStation(true);
		case ADD  : return new AddReservationStation(true);
		case MULT : return new MultReservationStation(true);
		case NAND : return new NandReservationStation(true);

		default : throw new RuntimeException("Not a valid reservation station type!");
		}
	}

	public void issueInstruction(short instruction, short destROB){
		issueInstructionSourceRegister1(InstructionDecoder.getRS(instruction));
		setBusy();
		this.destROB = destROB;
		ProcessorBuilder.getProcessor().getROB().getEntry(destROB).setType(InstructionDecoder.getOpcode(instruction));
		ProcessorBuilder.getProcessor().getROB().getEntry(destROB).setReady(false);
	}

	public void issueInstructionSourceRegister1(byte rs){

		if(ProcessorBuilder.getProcessor().getRegisterStatus(rs) != VALID){
			ReorderBufferEntry ROBEntry = ProcessorBuilder.getProcessor().getROB().getEntry(ProcessorBuilder.getProcessor().getRegisterStatus(rs));
			if(ROBEntry.isReady()){
				this.Vj = ROBEntry.getValue();
				this.Qj = 0;
			}
			else{
				this.Qj = ProcessorBuilder.getProcessor().getRegisterStatus(rs);
			}
		}
		else{
			this.Vj = ProcessorBuilder.getProcessor().getRegisterValue(rs);
			this.Qj = 0;
		}
	}

	public void issueInstructionSourceRegister2(byte rt){

		if(ProcessorBuilder.getProcessor().getRegisterStatus(rt) != VALID){
			ReorderBufferEntry ROBEntry = ProcessorBuilder.getProcessor().getROB().getEntry(ProcessorBuilder.getProcessor().getRegisterStatus(rt));
			if(ROBEntry.isReady()){
				this.Vk = ROBEntry.getValue();
				this.Qk = 0;
			}
			else{
				this.Qk = ProcessorBuilder.getProcessor().getRegisterStatus(rt);
			}
		}
		else{
			this.Vk = ProcessorBuilder.getProcessor().getRegisterValue(rt);
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
	
	public short getQj(){
		return Qj;
	}
	
	public short getQk(){
		return Qk;
	}
	
	public short getVj() {
		return Vj;
	}
	
	public void setVj(short value){
		Vj = value;
		Qj = 0;
	}
	
	public short getVk() {
		return Vk;
	}
	
	public void setVk(short value){
		Vk = value;
		Qk = 0;
	}
	
	public short getDestROB() {
		return destROB;
	}
	
	public short getAddress() {
		return address;
	}
	
	public void setAddress(short value) {
		address = value;
	}
	
	public void setTempReservationStation(ReservationStation rs) {
		tempRS = rs;
	}
}
