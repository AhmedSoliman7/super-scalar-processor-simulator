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
	
	int readHits;
	int writeHits;
	int readMisses;
	int writeMisses;

	public void configureCache(short size, short blockSize, short associativity, WritingPolicy writingPolicy) {
		this.size = size;
		this.blockSize = blockSize;
		this.associativity = associativity;
		this.writingPolicy = writingPolicy;
		
		readHits = writeHits = readMisses = writeMisses = 0;

		this.numberOfSets = (short) (size / (blockSize * associativity));
		this.sets = new CacheSet[this.numberOfSets];

		for (int i = 0; i < this.numberOfSets; i++) {
			this.sets[i] = new CacheSet(associativity);
		}
	}

	@Override
	public ReturnPair<Short> fetch(short address) {
		short blockNumber = (short) (address / this.blockSize);
		short setNumber = (short) (blockNumber % this.numberOfSets);
		short tag = (short) (blockNumber / this.numberOfSets);
		short offset = (short) (address % this.blockSize);

		short clockCycles = this.accessTime;
		
		CacheSet set = sets[setNumber];

		Short blockIndex = set.search(tag, offset);
		if (blockIndex != -1) {
			set.blocks[blockIndex].LRUCounter = set.LRUCounter++;
			readHits++;
			
			return new ReturnPair<Short>(this.accessTime, set.blocks[blockIndex].data[offset]);
		}
		
		readMisses++;
		
		short startAddress = (short) (address / this.blockSize * this.blockSize);
		short endAddress = (short) (startAddress + this.blockSize - 1);

		ReturnPair<CacheBlock> newBlockPair = fetchBlockFromNextLevel(startAddress, endAddress, tag, set);
		clockCycles += newBlockPair.clockCycles;
		CacheBlock newBlock = newBlockPair.value;

		int targetBlockIndex = set.targetBlockIndex();
		
		CacheBlock oldBlock = set.blocks[targetBlockIndex];
		if(oldBlock != null
				&& this.writingPolicy == WritingPolicy.WRITE_BACK
				&& ((WriteBackCacheBlock)oldBlock).dirty) {
			startAddress = (short) (((oldBlock.tag * this.numberOfSets) + setNumber) * blockSize);
			endAddress = (short) (startAddress + blockSize - 1);
			
			clockCycles += writeBlockToNextLevel(oldBlock, startAddress, endAddress);
		}
		
		set.blocks[targetBlockIndex] = newBlock;

		return new ReturnPair<Short>(clockCycles, newBlock.data[offset]);
	}
	
	private short writeBlockToNextLevel(CacheBlock block, short startAddress, short endAddress) {
		short clockCycles = 0;
		for(short i = startAddress; i <= endAddress; i++) {
			clockCycles += this.nextLevel.write(i, block.data[i - startAddress]);
		}
		
		return clockCycles;
	}

	private ReturnPair<CacheBlock> fetchBlockFromNextLevel(short startAddress, short endAddress, short tag, CacheSet set) {
		CacheBlock newBlock;
		if (this.writingPolicy == WritingPolicy.WRITE_BACK) {
			newBlock = new WriteBackCacheBlock(this.blockSize, tag, set.LRUCounter++);
		} else {
			newBlock = new WriteThroughCacheBlock(this.blockSize, tag, set.LRUCounter++);
		}

		short countCycles = 0;
	
		for (short i = startAddress; i <= endAddress; i++) {
			ReturnPair<Short> nextLevelReturn = nextLevel.fetch(i);
			
			countCycles += nextLevelReturn.clockCycles;
			
			newBlock.data[i - startAddress] = nextLevelReturn.value;
		}

		return new ReturnPair<CacheBlock>(countCycles, newBlock);
	}

	@Override
	public short write(short address, short value) {
		short blockNumber = (short) (address / this.blockSize);
		short setNumber = (short) (blockNumber % this.numberOfSets);
		short tag = (short) (blockNumber / this.numberOfSets);
		short offset = (short) (address % this.blockSize);
		
		CacheSet set = sets[setNumber];
		
		int blockIndex = set.search(tag, offset);
		
		short clockCycles = this.accessTime;
		
		if(blockIndex == -1) {
			writeMisses++;
			
			if(this.writingPolicy == WritingPolicy.WRITE_BACK) {
				short startAddress = (short) (address / this.blockSize * this.blockSize);
				short endAddress = (short) (startAddress + this.blockSize - 1);
				
				ReturnPair<CacheBlock> newBlockPair = fetchBlockFromNextLevel(startAddress, endAddress, tag, set);
				CacheBlock newBlock = newBlockPair.value;
				
				clockCycles += newBlockPair.clockCycles;
				
				blockIndex = set.targetBlockIndex();
				
				CacheBlock oldBlock = set.blocks[blockIndex];
				if(oldBlock != null
						&& this.writingPolicy == WritingPolicy.WRITE_BACK
						&& ((WriteBackCacheBlock)oldBlock).dirty) {
					startAddress = (short) (((oldBlock.tag * this.numberOfSets) + setNumber) * blockSize);
					endAddress = (short) (startAddress + blockSize - 1);
					
					clockCycles += writeBlockToNextLevel(oldBlock, startAddress, endAddress);
				}
				
				set.blocks[blockIndex] = newBlock;
			}
		}
		else {
			writeHits++;
		}
		
		if(blockIndex != -1) {
			set.blocks[blockIndex].data[offset] = value;
			set.blocks[blockIndex].LRUCounter = set.LRUCounter++;
		}
		
		if(this.writingPolicy == WritingPolicy.WRITE_THROUGH) {
			clockCycles += this.nextLevel.write(address, value);
		}
		else {
			((WriteBackCacheBlock) set.blocks[blockIndex]).dirty = true;
		}
		
		return clockCycles;
	}
	
	public CacheSet[] getSets() {
		return sets;
	}
	
	public int getReadHits() {
		return readHits;
	}

	public int getWriteHits() {
		return writeHits;
	}

	public int getReadMisses() {
		return readMisses;
	}

	public int getWriteMisses() {
		return writeMisses;
	}
}
