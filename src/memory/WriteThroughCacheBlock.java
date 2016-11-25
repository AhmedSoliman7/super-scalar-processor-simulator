package memory;

public class WriteThroughCacheBlock extends CacheBlock{
	
	public WriteThroughCacheBlock(int blockSize, short tag, int timeOfAccess) {
		super(blockSize, tag, timeOfAccess);
	}
}
