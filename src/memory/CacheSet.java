package memory;

public class CacheSet {
	CacheBlock[] blocks;
	int LRUCounter;
	int lastFreeBlock;
	int numberOfBlocks;

	public CacheSet(int numberOfBlocks) {
		this.numberOfBlocks = numberOfBlocks;
		
		blocks = new CacheBlock[numberOfBlocks];
		lastFreeBlock = 0;
	}

	public Short search(short tag, short offset) {
		for(int i = 0; i < this.blocks.length; i++) {
			if(this.blocks[i] == null){
				break;
			}

			if(this.blocks[i].tag == tag) {
				return this.blocks[i].data[offset];
			}
		}
		
		return null;
	}
	
	public void cacheBlock(CacheBlock block) {
		if(lastFreeBlock < numberOfBlocks) {
			blocks[lastFreeBlock++] = block;
			return;
		}
		
		// LRU
	}
}