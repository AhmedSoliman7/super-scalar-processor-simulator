package main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Main {

	public static void main(String[] args) throws FileNotFoundException {
		System.setIn(new FileInputStream("in.in"));
		Simulator simulator = new Simulator();
		simulator.run();
	}
}
