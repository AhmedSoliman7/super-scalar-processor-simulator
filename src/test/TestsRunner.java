package test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	MemoryTests.class,
	ParserTests.class,
	ProcessorBuilderTests.class,
	ProcessorTests.class,
	InstructionsTests.class,
	ForwardingTests.class,
	TrickyProgramsTest.class,
	SimulatorTests.class})
public class TestsRunner {
	@BeforeClass
    public static void init() throws FileNotFoundException {
        System.setOut(new PrintStream("test.out"));
    }
	
	@AfterClass
	public static void clean() {
        File f = new File("test.out");
        f.delete();
    }
}