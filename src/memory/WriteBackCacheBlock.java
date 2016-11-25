package memory;

public class WriteBackCacheBlock extends CacheBlock{
	public WriteBackCacheBlock(int blockSize, short tag, int timeOfAccess) {
		super(blockSize, tag, timeOfAccess); 
	}

	boolean dirty;
}
