package units;

import java.util.Arrays;

public class RegisterFile {

	static final short VALID = -1;
	private short[] registers;						
	private short[] registerStatus;
	private RegisterFile tempRegisterFile;
	
	public RegisterFile(int size, boolean isOriginal) {
		registers = new short[size];
		registerStatus = new short[size];
		clearStatus();
		if(isOriginal)
			tempRegisterFile = new RegisterFile(size, false);
	}
	
	public void clearStatus() {
		if(tempRegisterFile != null) {
				tempRegisterFile.clearStatus();
				return;
		}
		Arrays.fill(registerStatus, VALID);
	}

	public short getRegisterValue(byte register) {
		return registers[register];
	}
	
	public void setRegisterValue(byte register, short value) {
		if(tempRegisterFile != null) {
			tempRegisterFile.setRegisterValue(register, value);
			return;
		}
		registers[register] = value;
	}
	
	public short getRegisterStatus(byte register) {
		return registerStatus[register];
	}

	public void setRegisterStatus(byte register, short value) {
		if(tempRegisterFile != null) {
			tempRegisterFile.setRegisterStatus(register, value);
			return;
		}
		registerStatus[register] = value;
	}
	
	public void flush() {
		for(int i = 0; i < registers.length; ++i) {
			registers[i] = tempRegisterFile.registers[i];
			registerStatus[i] = tempRegisterFile.registerStatus[i];
		}
	}
}
