package memory;

public class WriteBackCacheBlock extends CacheBlock{
	public WriteBackCacheBlock(int blockSize, short tag, int timeOfAccess) {
		super(blockSize, tag, timeOfAccess); 
	}

	boolean dirty;
	
	@Override
	public void writeToMemory() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateValue(short address, short value) {
		// TODO Auto-generated method stub
		
	}

}
