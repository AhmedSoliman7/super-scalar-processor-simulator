package test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import memory.MemoryHandler;
import memory.ReturnPair;
import memory.WritingPolicy;

public class MemoryTests {
	@Test
	public void testOneLevelCacheWriteBack() {
		initHandler(1);
		handler.getDataCaches()[0].configureCache((short) 8, (short) 2, (short) 1, WritingPolicy.WRITE_BACK);
		handler.getMainMemory().setAccessTime((short) 5);
		handler.getDataCaches()[0].setAccessTime((short) 1);

		ReturnPair<Short> pair = handler.read((short)0);

		assertEquals(
				"Read value should be equal to 1.",
				1,
				pair.value.shortValue());
		assertEquals(
				"Number of cycles to access memory should be 11. 2 memory accesses + 1 cache access",
				11,
				pair.clockCycles);

		pair = handler.read((short) 1);

		assertEquals(
				"Read value should be equal to 2.",
				2,
				pair.value.shortValue());
		assertEquals(
				"Number of cycles to access memory should be 1. 0 memory accesses + 1 cache access",
				1,
				pair.clockCycles);

		pair = handler.read((short)3);

		assertEquals(
				"Read value should be equal to 1.",
				4,
				pair.value.shortValue());
		assertEquals(
				"Number of cycles to access memory should be 11. 2 memory accesses + 1 cache access",
				11,
				pair.clockCycles);

		pair = handler.read((short) 2);

		assertEquals(
				"Read value should be equal to 2.",
				3,
				pair.value.shortValue());
		assertEquals(
				"Number of cycles to access memory should be 1. 0 memory accesses + 1 cache access",
				1,
				pair.clockCycles);

		pair = handler.read((short) 8);

		assertEquals(
				"Read value should be equal to 9.",
				9,
				pair.value.shortValue());
		assertEquals(
				"Number of cycles to access memory should be 11. 2 memory accesses + 1 cache access",
				11,
				pair.clockCycles);

		pair = handler.read((short) 1);

		assertEquals(
				"Read value should be equal to 2.",
				2,
				pair.value.shortValue());
		assertEquals(
				"Number of cycles to access memory should be 1. 2 memory accesses + 1 cache access",
				11,
				pair.clockCycles);

		short writeCycles = handler.write((short) 10, (short) 50);
		assertEquals(
				"Memory value shouldn't be changed because policy is Write Back.",
				handler.getMainMemory().getData()[10],
				11);
		assertEquals(
				"Number of cycles to access memory should be 11. 2 memory accesses + 1 cache access",
				11,
				writeCycles);

		pair = handler.read((short) 10);

		assertEquals(
				"Read value should be equal to 50.",
				50,
				pair.value.shortValue());
		assertEquals(
				"Number of cycles to access memory should be 1. 0 memory accesses + 1 cache access",
				1,
				pair.clockCycles);

		pair = handler.read((short) 3);
		assertEquals(
				"Memory value at 10 should be 50 because dirty block is written back.",
				50,
				handler.getMainMemory().getData()[10]);
		assertEquals(
				"Memory value at 11 should remain unchanged.",
				12,
				handler.getMainMemory().getData()[11]);
		assertEquals(
				"Read value should be equal to 4.",
				4,
				pair.value.shortValue());
		assertEquals(
				"Number of cycles to access memory should be 1. 4 memory accesses + 1 cache access",
				21,
				pair.clockCycles);

		writeCycles = handler.write((short) 2, (short) 51);
		assertEquals(
				"Memory value shouldn't be changed because policy is Write Back.",
				handler.getMainMemory().getData()[2],
				3);
		assertEquals(
				"Number of cycles to access memory should be 1. 0 memory accesses + 1 cache access",
				1,
				writeCycles);

		writeCycles = handler.write((short) 11, (short) 52);
		assertEquals(
				"Memory value at 2 should be 51 because dirty block is written back.",
				handler.getMainMemory().getData()[2],
				51);
		assertEquals(
				"Number of cycles to access memory should be 1. 4 memory accesses + 1 cache access",
				21,
				writeCycles);

		assertEquals(
				"Number of read misses should be 5",
				5,
				handler.getDataCaches()[0].getReadMisses());

		assertEquals(
				"Number of read hits should be 3",
				3,
				handler.getDataCaches()[0].getReadHits());

		assertEquals(
				"Number of write misses should be 2",
				2,
				handler.getDataCaches()[0].getWriteMisses());

		assertEquals(
				"Number of write hits should be 1",
				1,
				handler.getDataCaches()[0].getWriteHits());
	}

	@Test
	public void testOneLevelCacheWriteThrough() {
		initHandler(1);
		handler.getDataCaches()[0].configureCache((short) 8, (short) 2, (short) 1, WritingPolicy.WRITE_THROUGH);
		handler.getMainMemory().setAccessTime((short) 5);
		handler.getDataCaches()[0].setAccessTime((short) 1);

		ReturnPair<Short> pair = handler.read((short)0);
		handler.read((short) 2);

		short writeCycles = handler.write((short) 10, (short) 50);
		assertEquals(
				"Memory value at 10 should be changed in write through.",
				50,
				handler.getMainMemory().getData()[10]);
		assertEquals(
				"Number of cycles to access memory should be 11. 1 memory accesses + 1 cache access",
				6,
				writeCycles);

		pair = handler.read((short) 10);

		assertEquals(
				"Read value should be equal to 50.",
				50,
				pair.value.shortValue());
		assertEquals(
				"Number of cycles to access memory should be 1. 2 memory accesses + 1 cache access",
				11,
				pair.clockCycles);

		assertEquals(
				"Number of read misses should be 3",
				3,
				handler.getDataCaches()[0].getReadMisses());

		assertEquals(
				"Number of read hits should be 0",
				0,
				handler.getDataCaches()[0].getReadHits());

		assertEquals(
				"Number of write misses should be 1",
				1,
				handler.getDataCaches()[0].getWriteMisses());

		assertEquals(
				"Number of write hits should be 0",
				0,
				handler.getDataCaches()[0].getWriteHits());
	}

	@Test
	public void testMultipleLevelCacheWriteBack() {
		initHandler(3);
		handler.getDataCaches()[0].configureCache((short) 4, (short) 2, (short) 1, WritingPolicy.WRITE_BACK);
		handler.getDataCaches()[1].configureCache((short) 8, (short) 2, (short) 2, WritingPolicy.WRITE_BACK);
		handler.getDataCaches()[2].configureCache((short) 16, (short) 4, (short) 4, WritingPolicy.WRITE_BACK);
		handler.getMainMemory().setAccessTime((short) 10);
		handler.getDataCaches()[0].setAccessTime((short) 1);
		handler.getDataCaches()[1].setAccessTime((short) 3);
		handler.getDataCaches()[2].setAccessTime((short) 5);

		ReturnPair<Short> pair = handler.read((short) 0);
		assertEquals("Read value should be 1",
				1,
				pair.value.shortValue());

		assertEquals("Access time should be 57. 4 Memory accesses + 2 L3 accesses + 2 L2 accesses + 1 L1 access",
				57,
				pair.clockCycles);	

		pair = handler.read((short) 3);
		assertEquals("Read value should be 4",
				4,
				pair.value.shortValue());

		assertEquals("Access time should be 17. 0 Memory accesses + 2 L3 accesses + 2 L2 accesses + 1 L1 access",
				17,
				pair.clockCycles);

		pair = handler.read((short) 5);
		assertEquals("Read value should be 6",
				6,
				pair.value.shortValue());

		assertEquals("Access time should be 57. 4 Memory accesses + 2 L3 accesses + 2 L2 accesses + 1 L1 access",
				57,
				pair.clockCycles);	

		pair = handler.read((short) 1);
		assertEquals("Read value should be 2",
				2,
				pair.value.shortValue());

		assertEquals("Access time should be 7. 0 Memory accesses + 0 L3 accesses + 2 L2 accesses + 1 L1 access",
				7,
				pair.clockCycles);

		pair = handler.read((short) 9);
		assertEquals("Read value should be 10",
				10,
				pair.value.shortValue());

		assertEquals("Access time should be 57. 4 Memory accesses + 2 L3 accesses + 2 L2 accesses + 1 L1 access",
				57,
				pair.clockCycles);

		pair = handler.read((short) 2);
		assertEquals("Read value should be 3",
				3,
				pair.value.shortValue());

		assertEquals("Access time should be 1. 0 Memory accesses + 0 L3 accesses + 0 L2 accesses + 1 L1 access",
				1,
				pair.clockCycles);

		pair = handler.read((short) 4);
		assertEquals("Read value should be 5",
				5,
				pair.value.shortValue());

		assertEquals("Access time should be 17. 0 Memory accesses + 2 L3 accesses + 2 L2 accesses + 1 L1 access",
				17,
				pair.clockCycles);

		short clockCycles = handler.write((short) 4, (short) 50);
		assertEquals("Value at cache level 2 should remain unchanged",
				5,
				handler.getDataCaches()[1].getSets()[0].getBlocks()[0].getData()[0]);

		assertEquals("Access time should be 1. 0 Memory accesses + 0 L3 accesses + 0 L2 accesses + 1 L1 access",
				1,
				clockCycles);

		clockCycles = handler.write((short) 8, (short) 51);
		assertEquals("Value should be written back in Write Back policy",
				50,
				handler.getDataCaches()[1].getSets()[0].getBlocks()[0].getData()[0]);

		assertEquals("Value at cache level 2 should remain unchanged",
				9,
				handler.getDataCaches()[1].getSets()[0].getBlocks()[1].getData()[0]);

		assertEquals("Access time should be 13. 0 Memory accesses + 0 L3 accesses + 4 L2 accesses + 1 L1 access",
				13,
				clockCycles);

		// test read/write hits/misses at cache level 1
		assertEquals(
				"Number of read misses at cache level 1 should be 6",
				6,
				handler.getDataCaches()[0].getReadMisses());

		assertEquals(
				"Number of read hits at cache level 1 should be 1",
				1,
				handler.getDataCaches()[0].getReadHits());

		assertEquals(
				"Number of write misses at cache level 1 should be 1",
				1,
				handler.getDataCaches()[0].getWriteMisses());

		assertEquals(
				"Number of write hits at cache level 1 should be 1",
				1,
				handler.getDataCaches()[0].getWriteHits());

		// test read/write hits/misses at cache level 2
		assertEquals(
				"Number of read misses at cache level 2 should be 5",
				5,
				handler.getDataCaches()[1].getReadMisses());

		assertEquals(
				"Number of read hits at cache level 2 should be 9",
				9,
				handler.getDataCaches()[1].getReadHits());

		assertEquals(
				"Number of write misses at cache level 2 should be 0",
				0,
				handler.getDataCaches()[1].getWriteMisses());

		assertEquals(
				"Number of write hits at cache level 2 should be 2",
				2,
				handler.getDataCaches()[1].getWriteHits());

		// test read/write hits/misses at cache level 3
		assertEquals(
				"Number of read misses at cache level 3 should be 3",
				3,
				handler.getDataCaches()[2].getReadMisses());

		assertEquals(
				"Number of read hits at cache level 3 should be 7",
				7,
				handler.getDataCaches()[2].getReadHits());

		assertEquals(
				"Number of write misses at cache level 3 should be 0",
				0,
				handler.getDataCaches()[2].getWriteMisses());

		assertEquals(
				"Number of write hits at cache level 3 should be 0",
				0,
				handler.getDataCaches()[2].getWriteHits());

	}

	@Test
	public void testMultipleLevelCacheWriteThrough() {
		initHandler(3);
		handler.getDataCaches()[0].configureCache((short) 4, (short) 2, (short) 1, WritingPolicy.WRITE_THROUGH);
		handler.getDataCaches()[1].configureCache((short) 8, (short) 2, (short) 2, WritingPolicy.WRITE_THROUGH);
		handler.getDataCaches()[2].configureCache((short) 16, (short) 4, (short) 4, WritingPolicy.WRITE_THROUGH);
		handler.getMainMemory().setAccessTime((short) 10);
		handler.getDataCaches()[0].setAccessTime((short) 1);
		handler.getDataCaches()[1].setAccessTime((short) 3);
		handler.getDataCaches()[2].setAccessTime((short) 5);

		handler.read((short) 0);
		handler.read((short) 3);
		handler.read((short) 5);
		handler.read((short) 1);
		handler.read((short) 9);
		handler.read((short) 2);
		handler.read((short) 4);

		short clockCycles = handler.write((short) 4, (short) 50);
		assertEquals("Value at memory should be changed in Write Through",
				50,
				handler.getMainMemory().getData()[4]);

		assertEquals("Access time should be 19. 1 main memory access + 1 L3 access + 1 L2 access + 1 L1 access",
				19,
				clockCycles);

		clockCycles = handler.write((short) 8, (short) 51);
		assertEquals("Value should be written back in Write Back policy",
				51,
				handler.getMainMemory().getData()[8]);

		ReturnPair<Short> pair = handler.read((short) 8); 
		assertEquals("Value is updated correctly.",
				51,
				pair.value.shortValue());

		assertEquals("Access time should be 7. 0 Memory accesses + 0 L3 accesses + 2 L2 accesses + 1 L1 access",
				7,
				pair.clockCycles);

		// test read/write hits/misses at cache level 1
		assertEquals(
				"Number of read misses at cache level 1 should be 7",
				7,
				handler.getDataCaches()[0].getReadMisses());

		assertEquals(
				"Number of read hits at cache level 1 should be 1",
				1,
				handler.getDataCaches()[0].getReadHits());

		assertEquals(
				"Number of write misses at cache level 1 should be 1",
				1,
				handler.getDataCaches()[0].getWriteMisses());

		assertEquals(
				"Number of write hits at cache level 1 should be 1",
				1,
				handler.getDataCaches()[0].getWriteHits());

		// test read/write hits/misses at cache level 2
		assertEquals(
				"Number of read misses at cache level 2 should be 5",
				5,
				handler.getDataCaches()[1].getReadMisses());

		assertEquals(
				"Number of read hits at cache level 2 should be 9",
				9,
				handler.getDataCaches()[1].getReadHits());

		assertEquals(
				"Number of write misses at cache level 2 should be 0",
				0,
				handler.getDataCaches()[1].getWriteMisses());

		assertEquals(
				"Number of write hits at cache level 2 should be 2",
				2,
				handler.getDataCaches()[1].getWriteHits());

		// test read/write hits/misses at cache level 3
		assertEquals(
				"Number of read misses at cache level 3 should be 3",
				3,
				handler.getDataCaches()[2].getReadMisses());

		assertEquals(
				"Number of read hits at cache level 3 should be 7",
				7,
				handler.getDataCaches()[2].getReadHits());

		assertEquals(
				"Number of write misses at cache level 3 should be 0",
				0,
				handler.getDataCaches()[2].getWriteMisses());

		assertEquals(
				"Number of write hits at cache level 3 should be 2",
				2,
				handler.getDataCaches()[2].getWriteHits());
	}

	MemoryHandler handler;

	void initHandler(int numberOfCaches) {
		handler = new MemoryHandler(numberOfCaches);

		for(short i = 1; i <= 20; i++) {
			handler.getMainMemory().getData()[i - 1] = i;
		}
	}
}
