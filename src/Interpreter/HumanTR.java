package Interpreter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import Tuple.Tuple;

/**
 * @author benzhang tang
 * HumanTR class class offers an efficient way to read tuples from a normal character file of tuples
 * Feature: Buffer reader is used and the file is read line by line. 
 * A tuple is a line of ints separated by commas 
 */
public class HumanTR implements TupleReader{
	private File file;  //file of table to be read
	private BufferedReader br; // A  buffered reader for the file
	
	/**
	 * constructor for the class returns the object containing file and and buffered reader for the file
	 * @param file
	 * @throws FileNotFoundException
	 */
	public HumanTR (File file) throws FileNotFoundException {
		this.file=file;
		br = new BufferedReader(new FileReader(file));
	}

	@Override
	/**
	 * ReadNextTuple method returns the next "tuple" in form of string in the file if there is one; 
	 * it returns null if eof.
	 */
	public String ReadNextTuple() throws IOException {
		String line = br.readLine();
		if (line == null) return null;
		return line;
	}

	@Override
	// close the reader
	public void close() throws IOException {
		br.close(); 
	}

	@Override
	public void reset() throws IOException {
		if (br != null) {
			br.close();
		}
		br = new BufferedReader(new FileReader(file));
	}

	
}
