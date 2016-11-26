package test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.junit.Test;

import main.ProcessorBuilder;
import memory.MemoryHandler;
import memory.ReturnPair;

public class ProcessorBuilderTests {

	@Test
	public void testProcessorBuilder() throws Exception {
		initAssembly();
		initUserInput();

		ProcessorBuilder builder = new ProcessorBuilder();
		builder.buildProcessor(new FileInputStream(USR_FILE_NAME));

		MemoryHandler handler = builder.getProcessor().getMemoryUnit();
		
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

		clean();
	}

	static final String ASM_FILE_NAME = "testFile.asm";
	static final String USR_FILE_NAME = "testFile.usr";

	void initAssembly() throws FileNotFoundException {
		PrintWriter out = new PrintWriter(ASM_FILE_NAME);

		out.println("ADD r1,r2 r7");
		out.println("SuB R7, r6 4");
		out.println("NAND r1, r5, r3");
		out.println("MULT r7, r7, r7");
		out.println("LW r6, r7, 32");
		out.println("SW r1, r6, -16");
		out.println("ADDI r2, r1, 15");
		out.println("beq r7, r5, 4");
		out.println("jmp r2, 13");
		out.println("JALR r1, r2");
		out.println("RET r1");

		out.flush();
		out.close();
	}

	void initUserInput() throws FileNotFoundException {
		PrintWriter out = new PrintWriter(USR_FILE_NAME);

		out.println("3");
		out.println("10");
		out.println("4 2 1 0 1");
		out.println("8 2 2 0 3");
		out.println("16 4 4 0 5");
		out.println("2");
		out.println("5");
		out.println("5");
		out.println("1 2");
		out.println("1 2");
		out.println("1 2");
		out.println("1 2");
		out.println("1 2");
		out.println(ASM_FILE_NAME);
		out.println("100");
		out.println("20");
		for(int i = 0; i < 20; i++)
			out.printf("%d %d\n", i, i + 1);

		out.flush();
		out.close();
	}

	void clean() {
		File file = new File(ASM_FILE_NAME);
		file.delete();

		file = new File(USR_FILE_NAME);
		file.delete();
	}
}
