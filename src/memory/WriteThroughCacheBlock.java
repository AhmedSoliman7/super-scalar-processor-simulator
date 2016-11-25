package memory;

public class WriteThroughCacheBlock extends CacheBlock{
	
	public WriteThroughCacheBlock(int blockSize, short tag, int timeOfAccess) {
		super(blockSize, tag, timeOfAccess);
	}

	@Override
	public void writeToMemory() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateValue(short address, short value) {
		// TODO Auto-generated method stub
		
	}

}
