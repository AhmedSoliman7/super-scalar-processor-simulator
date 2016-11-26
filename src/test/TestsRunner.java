package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ MemoryTests.class, ParserTests.class, ProcessorBuilderTests.class})
public class TestsRunner {

}
