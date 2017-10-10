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
		String input = "/Users/benzhangtang/Desktop/cs4321/project3/samples/input/db/data/boat.txt";
		//1. read the file into steam	
		try {
			FileInputStream fis = new FileInputStream(input);

			//2. allocate a channel to read file
			FileChannel channel = fis.getChannel();
			//			 long fileSize = channel.size(); 
			//			 System.out.println(fileSize);

			//3. allocate a buffer to read the file in the fixed-size chunks, and initialize it
			buffer = ByteBuffer.allocate( 1024 * 4);
			buffer.clear();
			buffer.putInt( 0, Num_Attributes);
			buffer.putInt( 5, TupleNum_on_page);
			
			//			 int remind = (1024-2) % Num_Attributes;
			//			 int total_tuples = (1024-2-remind)/Num_Attributes;
			//			 int current_tuple = 0;

			//initialize a array for tuples to be stored into buffer in a bunch
			int[] tupleArr = new int [(int)channel.size()/4];

			//len is the number of bytes read
			long len = 0;
			//The offset within the array of the first byte to be written
			int offset =  2;

			//Reads a sequence of bytes from this channel into the given buffer until the channel is empty 
			while ((len = channel.read(buffer))!= -1) {
				//convert the buffer from writing data to buffer from disk to reading mode
				buffer.flip();

				//transfers bytes from this buffer into the given destination array. 
				//If there are fewer bytes remaining in the buffer than are required to satisfy the request, 
				//that is, if length > remaining(), then no bytes are transferred and a BufferUnderflowException is thrown.
				buffer.asIntBuffer().get(tupleArr,offset,(int)len/4);
				//next position in buffer to start
				offset += (int)len/4;
				buffer.clear();
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
