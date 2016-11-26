package units;

public class InstructionInFetch {
	private short instruction;
	private short remainingCycles;
	
	public InstructionInFetch(short instruction, short cycles) {
		this.instruction = instruction;
		this.remainingCycles = cycles;
	}
	
	public boolean isReady() {
		return remainingCycles == 0;
	}
	
	public void decrementCycles() {
		if(this.remainingCycles > 0) {
			this.remainingCycles--;
		}
	}
	
	public short getInstruction() {
		return this.instruction;
	}
}
