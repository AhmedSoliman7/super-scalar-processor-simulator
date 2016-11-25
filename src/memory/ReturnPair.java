package memory;

public class ReturnPair<T>{
	public short clockCycles;
	public T value;
	
	public ReturnPair(short clockCycles, T value) {
		this.clockCycles = clockCycles;
		this.value = value;
	}
}
