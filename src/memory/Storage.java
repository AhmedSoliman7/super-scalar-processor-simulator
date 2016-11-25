package memory;

public abstract class Storage {
	int accessTime;
	
	public abstract short fetch(short address);
	public abstract void write(short address, short value);
}