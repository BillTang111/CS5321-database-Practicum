package Interpreter;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import Tuple.Tuple;

public class BinaryTR implements TupleReader {
	
	// int Num_Attributes;
	int TupleNum_on_page;
	ByteBuffer buffer;
	
  public BinaryTR(int Num_Attributes) throws IOException  {
	  String input = "readme.txt";
		//1. read the file into steam	File t = new File("/Users/benzhangtang/Desktop/cs4321/project3/samples/input/db/data/Boats");
		try {
			FileInputStream fis = new FileInputStream(input);
			
			//2. // allocate a channel to read file
			 FileChannel channel = fis.getChannel();
			 
			//3. allocate a buffer to read the file in the fixed-size chunks, and initialize it
			 buffer = ByteBuffer.allocate( 1024 * 4);
			 buffer.clear();
			 buffer.putInt( 0, Num_Attributes);
			 buffer.putInt(1, TupleNum_on_page);
			 int remind = (1024-2) % Num_Attributes;
			 int total_tuples = (1024-2-remind)/Num_Attributes;
			 int current_tuple = 0;
			 
			 //
			 while (current_tuple != total_tuples) {
				 
			 }
			 
		     //4.  read a page of raw bytes, up to 6k bytes to buffer till -1 meaning eof.
			 
		      channel.read( buffer );
			 
		     //5. 
			 
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
