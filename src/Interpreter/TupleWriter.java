package Interpreter;

import java.io.*;

import Tuple.Tuple;

public interface TupleWriter {

	//method to write a tuple from a channel
	public void WriteTuple (Tuple t) throws IOException;
	
	
}
