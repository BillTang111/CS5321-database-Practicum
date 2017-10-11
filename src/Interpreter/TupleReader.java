package Interpreter;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;

import Tuple.Tuple;

public interface TupleReader {
	
	//method to read a tuple from the file to channel
	public String ReadNextTuple () throws IOException;
	
	//method to close this file 
	//public void close() throws IOException;
	
	//method to reset the reading pointer location to start of the file page
	public void reset() throws IOException;
	
    /**
     * For debugging use.
     * Print all tuples retrieved to Console.
     */
    void dump();

	//void close();
}
