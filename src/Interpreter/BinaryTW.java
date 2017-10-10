package Interpreter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import Tuple.Tuple;

public class BinaryTW implements TupleWriter{
	FileOutputStream fout;
	FileChannel fc;
	ByteBuffer buffer;
	
	public BinaryTW(String outputLocation){
	try {
		fout = new FileOutputStream( outputLocation );
		fc = fout.getChannel();
	    buffer = ByteBuffer.allocate( 1024 * 4);
	    
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}	
		
	}
	
	// write the next tuple to the output stream
	@Override
	public void WriteTuple(Tuple t) {
		// TODO Auto-generated method stub
		
	}

}
