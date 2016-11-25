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

	public short search(short tag, short offset) {
		for(short i = 0; i < this.blocks.length; i++) {
			if(this.blocks[i] == null){
				break;
			}

			if(this.blocks[i].tag == tag) {
				return i;
			}
		}
		
		return -1;
	}
	
	public int targetBlockIndex() {
		if(lastFreeBlock < numberOfBlocks) {
			return lastFreeBlock++;
		}
		
		short LRUi = LRUBlockIndex();
		return LRUi;
	}
	
	private short LRUBlockIndex() {
		short LRUi = 0;
		
		for(short i = 1; i < numberOfBlocks; i++)
			if(blocks[i].LRUCounter < blocks[LRUi].LRUCounter)
				LRUi = i;
		
		return LRUi;
	}
}