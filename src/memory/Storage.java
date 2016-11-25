package memory;

public abstract class Storage {
	short accessTime;
	
	public abstract ReturnPair<Short> fetch(short address);
	public abstract short write(short address, short value);
	
	public short getAccessTime() {
		return accessTime;
	}
	
	public void setAccessTime(short accessTime) {
		this.accessTime = accessTime;
	}
}