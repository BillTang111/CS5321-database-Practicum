package Interpreter;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;

import Tuple.Tuple;

public interface TupleReader {
	

	String input = "readme.txt";
	//1. read the file into steam	File t = new File("/Users/benzhangtang/Desktop/cs4321/project3/samples/input/db/data/Boats");
	FileInputStream fis = new FileInputStream(input);
	
	
	//2. // allocate a channel to read file
	 FileChannel channel = fis.getChannel();
	 
	//3. allocate a buffer to read the file in the fixed-size chunks
	 ByteBuffer buffer = ByteBuffer.allocate( 1024 * 4);
	 
	//4. clear buffer for use
	 //buffer.clear();
	
	
	
}
