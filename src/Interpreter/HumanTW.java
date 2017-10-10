package Interpreter;

import java.io.BufferedReader;
import java.io.File;

import Tuple.Tuple;

/**
 * @author benzhang tang
 * HumanTW class class offers an efficient way to write tuples to a file of tuples
 * Feature: Buffer writer is used and the file is read line by line. 
 * A tuple is a line of ints separated by commas  
 */
public class HumanTW implements TupleWriter{
	private File file;
	private BufferedReader bw;
	

	@Override
	public void WriteTuple(Tuple t) {
		// TODO Auto-generated method stub
		
	}

}
