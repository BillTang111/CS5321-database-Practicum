package Interpreter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import Tuple.Tuple;

public class BinaryTR implements TupleReader {
	
  public BinaryTR()  {
	  String input = "readme.txt";
		//1. read the file into steam	File t = new File("/Users/benzhangtang/Desktop/cs4321/project3/samples/input/db/data/Boats");
		try {
			FileInputStream fis = new FileInputStream(input);
			
			//2. // allocate a channel to read file
			 FileChannel channel = fis.getChannel();
			 
			//3. allocate a buffer to read the file in the fixed-size chunks
			 ByteBuffer buffer = ByteBuffer.allocate( 1024 * 4);
			 
			//4. 
			 
			 
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
  }

@Override
public Tuple ReadNextTuple() {
	// TODO Auto-generated method stub
	return null;
}

@Override
public void close() {
	// TODO Auto-generated method stub
	
}

@Override
public void reset() {
	// TODO Auto-generated method stub
	
}
}
