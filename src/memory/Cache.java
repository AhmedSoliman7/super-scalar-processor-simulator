package memory;

public class Cache extends Storage {
	short size;
	short blockSize;
	short associativity;

	CacheSet[] sets;
	WritingPolicy writingPolicy;
	Storage nextLevel;

	short numberOfSets;

	public Cache(Storage nextLevel) {
		this.nextLevel = nextLevel;
	}

	public void configureCache(short size, short blockSize, short associativity, WritingPolicy writingPolicy) {
		this.size = size;
		this.blockSize = blockSize;
		this.associativity = associativity;
		this.writingPolicy = writingPolicy;

		this.numberOfSets = (short) (size / (blockSize * associativity));
		this.sets = new CacheSet[this.numberOfSets];

		for (int i = 0; i < this.numberOfSets; i++) {
			this.sets[i] = new CacheSet(associativity);
		}
	}

	@Override
	public short fetch(short address) {
		short blockNumber = (short) (address / this.blockSize);
		short setNumber = (short) (blockNumber % this.numberOfSets);
		short tag = (short) (blockNumber / this.numberOfSets);
		short offset = (short) (address % this.blockSize);

		CacheSet set = sets[setNumber];

		Short blockIndex = set.search(tag, offset);
		if (blockIndex != -1) {
			set.blocks[blockIndex].LRUCounter = set.LRUCounter++;
			return set.blocks[blockIndex].data[offset];
		}

		short startAddress = (short) (address / this.blockSize * this.blockSize);
		short endAddress = (short) (startAddress + this.blockSize - 1);

		CacheBlock newBlock = fetchBlockFromNextLevel(startAddress, endAddress, tag, set);

		set.cacheBlock(newBlock);

		return newBlock.data[offset];
	}

	private CacheBlock fetchBlockFromNextLevel(short startAddress, short endAddress, short tag, CacheSet set) {
		CacheBlock newBlock;
		if (this.writingPolicy == WritingPolicy.WRITE_BACK) {
			newBlock = new WriteBackCacheBlock(this.blockSize, tag, set.LRUCounter++);
		} else {
			newBlock = new WriteThroughCacheBlock(this.blockSize, tag, set.LRUCounter++);
		}

		for (short i = startAddress; i <= endAddress; i++) {
			newBlock.data[i - startAddress] = nextLevel.fetch(i);
		}

		return newBlock;
	}

	@Override
	public void write(short address, short value) {
		short blockNumber = (short) (address / this.blockSize);
		short setNumber = (short) (blockNumber % this.numberOfSets);
		short tag = (short) (blockNumber / this.numberOfSets);
		short offset = (short) (address % this.blockSize);
		
		CacheSet set = sets[setNumber];
		
		int blockIndex = set.search(tag, offset);
		
		if(blockIndex == -1) {
			if(this.writingPolicy == WritingPolicy.WRITE_BACK) {
				short startAddress = (short) (address / this.blockSize * this.blockSize);
				short endAddress = (short) (startAddress + this.blockSize - 1);
				
				CacheBlock newBlock = fetchBlockFromNextLevel(startAddress, endAddress, tag, set);
				
				blockIndex = set.cacheBlock(newBlock);
			}
		}
		
		if(blockIndex != -1) {
			set.blocks[blockIndex].data[offset] = value;
			set.blocks[blockIndex].LRUCounter = set.LRUCounter++;
		}
		
		if(this.writingPolicy == WritingPolicy.WRITE_THROUGH) {
			this.nextLevel.write(address, value);
		}
		else {
			((WriteBackCacheBlock) set.blocks[blockIndex]).dirty = true;
		}
	}
}
