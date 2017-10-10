package Interpreter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import Tuple.Tuple;

public class BinaryTW implements TupleWriter{
	FileOutputStream fout;
	FileChannel fc;
	ByteBuffer buffer;
	int index;
	int numAttributes;
	int numTuples;
	
	public BinaryTW(String outputLocation){
	try {
		fout = new FileOutputStream( outputLocation );
		fc = fout.getChannel();
	    buffer = ByteBuffer.allocate( 1024 * 4);
	    index=0;
	    
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}	
		
	}
	
	// write the next tuple to the output stream
	@Override
	public void WriteTuple(Tuple t) {
		// TODO Auto-generated method stub
		//put the attribute num and tuple num as the first eight bytes
		ArrayList tupleList = t.getTuple();
		if (index == 0) {
            numAttributes = tupleList.size();
            numTuples = (1024 - 2) / numAttributes;
            buffer.putInt(numAttributes);
            buffer.putInt(numTuples);
            index += 8;
        }
		
		//write tuple data to the buffer page
		for (int i = 0; i < numAttributes; i++) {
            buffer.putInt((int)tupleList.get(i));
            //lastPageTuples.add(attributes.get(i));
            index += 4;
        }
		
		//if the whole buffer page is full
		if (index == 4 * (2 + numTuples * numAttributes)) {
            
            while (index < 1024*4) {
                buffer.putInt(0);
                index += 4;
            }
            // write to the buffer
            try {
                buffer.flip();
                fc.write(buffer);
                if (buffer.hasRemaining()) {
                    buffer.compact();
                } else {
                    buffer.clear();
                }
                index = 0;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
		
		
		
		
    }
	

}
