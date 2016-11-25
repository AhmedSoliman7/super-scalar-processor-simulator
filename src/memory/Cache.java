package memory;

public class Cache extends Storage{
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
		
		for(int i = 0; i < this.numberOfSets; i++) {
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
		
		Short answer = set.search(tag, offset);
		if(answer != null) {
			return answer.shortValue();
		}
				
		short startAddress = (short) (address / this.blockSize * this.blockSize);
		short endAdress = (short) (startAddress + this.blockSize - 1);
		
		CacheBlock newBlock;
		if(this.writingPolicy == WritingPolicy.WRITE_BACK) {
			newBlock = new WriteBackCacheBlock(this.blockSize, tag, set.LRUCounter++);
		}
		else {
			newBlock = new WriteThroughCacheBlock(this.blockSize, tag, set.LRUCounter++);
		}
		
		for(short i = startAddress; i <= endAdress; i++) {
			newBlock.data[i - startAddress] = nextLevel.fetch(i);
		}
		
		set.cacheBlock(newBlock);
		
		return newBlock.data[offset];
	}

	@Override
	public void write(short address, short value) {
		// TODO Auto-generated method stub
		
	}
}
