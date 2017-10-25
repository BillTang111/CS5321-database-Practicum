package Interpreter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import Tuple.Tuple;

/**
 * @author Lini Tan
 * 
 * BinaryTW class is used to write tuple into file
 */
public class BinaryTW implements TupleWriter{
	FileOutputStream fout;
	FileChannel fc;
	ByteBuffer buffer;
	int index;
	int numAttributes;
	int numTuples;
	private ByteBuffer copy;
    private List<Integer> lastPage = new LinkedList<>();
	
	public BinaryTW(String outputLocation){
	try {
		fout = new FileOutputStream( outputLocation );
		fc = fout.getChannel();
	    buffer = ByteBuffer.allocate( 1024 * 4);
	    copy = ByteBuffer.allocate( 1024 * 4);
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
			int entry = Integer.parseInt((String) tupleList.get(i));
            buffer.putInt(entry);
            lastPage.add(entry);
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
                copy = buffer.duplicate();
                index = 0;
                lastPage.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }		
    }
	
	/**
     * Close the output stream.
     */
    @Override
    public void close() {
        try {
            // fill last page
            if (index != 0) {
                numTuples = lastPage.size() / numAttributes;
                copy.putInt(numAttributes);
                copy.putInt(numTuples);
                for (Integer n : lastPage) {
                    copy.putInt(n);
                }
                while (index < 1024*4) {
                    copy.putInt(0);
                    index += 4;
                }
                copy.flip();
                fc.write(copy);
                if (copy.hasRemaining()) {
                    copy.compact();
                }
            }
            if (copy.hasRemaining()) {
                copy.flip();
                fc.write(copy);
            }
            // close output stream
            fout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
