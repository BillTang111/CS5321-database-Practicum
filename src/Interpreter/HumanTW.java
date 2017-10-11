package Interpreter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
	public void WriteTuple(Tuple t) throws IOException {
		// TODO Auto-generated method stub
		this.file = file;
		FileWriter fw;
		try {
			fw = new FileWriter(file);
	        BufferedWriter bw = new BufferedWriter(fw);
	        bw.write(t.getTuple().toString());
	        bw.newLine();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void close() {
		try {
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
