package units;

public class InstructionInFetch {
	private InstructionPair<Short> instructionPair;
	private short remainingCycles;
	
	public InstructionInFetch(short instruction, short instructionAddress, short cycles) {
		this.instructionPair = new InstructionPair<Short>(instruction, instructionAddress);
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
	
	public InstructionPair<Short> getInstruction() {
		return instructionPair;
	}
}
