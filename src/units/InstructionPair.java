package units;

public class InstructionPair <T> {

	T instruction, address;
	
	InstructionPair(T instruction, T address) {
		this.instruction = instruction;
		this.address = address;
	}
	
	public T getInstruction() {
		return instruction;
	}
	
	public T getAddress() {
		return address;
	}
}
