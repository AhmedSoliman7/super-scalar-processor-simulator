package reservation_station;

import main.ProcessorBuilder;
import units.InstructionDecoder;
import units.ReorderBufferEntry;

public abstract class ReservationStation {

	private static final int VALID = -1;
	private short Qj, Qk, Vj, Vk, destROB, address;
	private boolean busy;
	private ReservationStation tempRS;
	
	public static ReservationStation create(ReservationStationType reservationStationType) { 
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
		this.setDestROB(destROB);
		ProcessorBuilder.getProcessor().getROB().getEntry(destROB).setType(InstructionDecoder.getOpcode(instruction));
		ProcessorBuilder.getProcessor().getROB().getEntry(destROB).setReady(false);
	}

	public void issueInstructionSourceRegister1(byte rs){

		if(ProcessorBuilder.getProcessor().getRegisterStatus(rs) != VALID){
			ReorderBufferEntry ROBEntry = ProcessorBuilder.getProcessor().getROB().getEntry(ProcessorBuilder.getProcessor().getRegisterStatus(rs));
			if(ROBEntry.isReady()){
				this.setVj(ROBEntry.getValue());
			}
			else{
				this.setQj(ProcessorBuilder.getProcessor().getRegisterStatus(rs));
			}
		}
		else{
			this.setVj(ProcessorBuilder.getProcessor().getRegisterValue(rs));
		}
	}

	public void issueInstructionSourceRegister2(byte rt){

		if(ProcessorBuilder.getProcessor().getRegisterStatus(rt) != VALID){
			ReorderBufferEntry ROBEntry = ProcessorBuilder.getProcessor().getROB().getEntry(ProcessorBuilder.getProcessor().getRegisterStatus(rt));
			if(ROBEntry.isReady()){
				this.setVk(ROBEntry.getValue());
			}
			else{
				this.setQk(ProcessorBuilder.getProcessor().getRegisterStatus(rt));
			}
		}
		else{
			this.setVk(ProcessorBuilder.getProcessor().getRegisterValue(rt));
		}
	}

	public abstract void executeInstruction();

	public abstract void writeInstruction();
	
	public boolean isBusy() {
		return busy;
	}
	
	public void setBusy() {
		if(tempRS != null) {
			tempRS.setBusy();
			return;
		}
		busy = true;
	}
	
	public void clearBusy() {
		if(tempRS != null) {
			tempRS.clearBusy();
			return;
		}
		busy = false;
	}
	
	public short getQj(){
		return Qj;
	}
	
	public void setQj(short value) {
		if(tempRS != null) {
			tempRS.setQj(value);
			return;
		}
		Qj = 0;
	}
	
	public void setQk(short value) {
		if(tempRS != null) {
			tempRS.setQk(value);
			return;
		}
		Qk = 0;
	}
	
	public short getQk(){
		return Qk;
	}
	
	public short getVj() {
		return Vj;
	}
	
	public void setVj(short value){
		if(tempRS != null) {
			tempRS.setVj(value);
			return;
		}
		Vj = value;
		Qj = 0;
	}
	
	public short getVk() {
		return Vk;
	}
	
	public void setVk(short value){
		if(tempRS != null) {
			tempRS.setVk(value);
			return;
		}
		Vk = value;
		Qk = 0;
	}
	
	public short getDestROB() {
		return destROB;
	}
	
	private void setDestROB(short value) {
		if(tempRS != null) {
			tempRS.setDestROB(value);
			return;
		}
		destROB = value;
	}
	
	public short getAddress() {
		return address;
	}
	
	public void setAddress(short value) {
		if(tempRS != null) {
			tempRS.setAddress(value);
			return;
		}
		address = value;
	}
	
	public void setTempReservationStation(ReservationStation rs) {
		tempRS = rs;
	}
	
	public void flush() {
		
	}
}
