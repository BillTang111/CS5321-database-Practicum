package Interpreter;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;

import Tuple.Tuple;

public interface TupleReader {
	
	//method to read a tuple from the file to channel
	public Tuple ReadNextTuple ();
	
	//method to close this file 
	public void close();
	
	//method to reset the reading pointer location to start of the file page
	public void reset();

	
	
	
}
