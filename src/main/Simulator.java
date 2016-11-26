package main;

import units.Processor;

public class Simulator {

	private Processor processor;
	
	public Simulator(){
		/*
		 * TODO: Inputs and Initialization
		 * 	+ Memory:
		 * 		- no. of cache levels
		 * 		- for each cache level: (1) geometry, (2) write policy for hit and miss. (3) cycles for data access
		 * 		- main memory access time (in cycles)
		 *  => Initialize memory with capacity 64KB using inputs
		 *  
		 *  + Hardware Organization:
		 *  	- pipeline width (# issued instructions per cycle)
		 *  	- size of instruction queue
		 *  	- number of reservation station for each class (check ReservationStationType.java)
		 *  	- number of ROB entries
		 *  	- number of cycles for each functional unit (each different class)
		 *  => Initialize processor using inputs
		 *   
		 *  + Assembly program:
		 *  	- take assembly program from user and the starting address of the program in memory
		 *  => Parse program and save it in memory
		 *  
		 *  + Program data:
		 *  	- take initial data from user if any (memory address + value)
		 *  => Add data to memory
		 *   
		 */
		
		//TODO: call run() to start simulation
		
		 
	}
	
	void run(){
		
		/*
		 * TODO
		 * Count
		 * # instructions completed
		 * # branches encountered
		 * # cycles spanned
		 * # cache accesses and misses in each level
		 * # branch misprediction
		 * 
		 * Handle simultaenously running instructions
		 */
	}
}
