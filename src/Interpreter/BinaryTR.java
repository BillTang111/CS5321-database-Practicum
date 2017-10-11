package Interpreter;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import Tuple.Tuple;

/**
 * @author benzhang tang
 * 
 * BinaryTR class offers an efficient way to read tuples from a binary file of tuples 
 * File format: input file is a sequence of pages of (4*1024) bytes. # of attributes and # of tuples on the page
 * are two metadata stored in each page. Each field of the tuple is stored as 4 bytes integer. 
 * Feature: Java NIO is used in this class to increase the speed of I/O
 */
public class BinaryTR implements TupleReader {

	int Num_Attributes;            // number of attributes in a tuple
	int TupleNum_on_page;		  // number of tuples in a page
	private static final int buffer_size = 4*1024; // size of the buffer page
	private ByteBuffer buffer;     //buffer page
	private File file;			//file name

/*
 * constructor of the class with input parameter of file of table
 */
	public BinaryTR(File file) throws IOException  {
		File input = file;
		//1. read the file into steam	
		try {
			FileInputStream fis = new FileInputStream(input);

			//2. allocate a channel to read file
			FileChannel channel = fis.getChannel();

			//3. allocate a buffer to read the file in the fixed-size chunks, and initialize it
			buffer = ByteBuffer.allocate(buffer_size);
			buffer.clear();
//			buffer.putInt( 0, Num_Attributes);
//			buffer.putInt( 5, TupleNum_on_page);

//			int remind = (1024-2) % Num_Attributes;
//			int total_tuples = (1024-2-remind)/Num_Attributes;
//			int current_tuple = 0;

			//initialize a array for tuples to be stored into buffer in a bunch
//			int[] tupleArr = new int [(int)channel.size()/4];
			int[] tupleArr = new int [Num_Attributes];

			//len is the number of bytes read
			long len = 0;
			//The offset within the array of the first byte to be written
			int offset =  8;

			//Reads a sequence of bytes from this channel into the given buffer until the channel is empty 
			while ((len = channel.read(buffer))!= -1) {
				//convert the buffer from writing data to buffer from disk to reading mode
				buffer.flip();

				//transfers bytes from this buffer into the given destination array. 
				//If there are fewer bytes remaining in the buffer than are required to satisfy the request, 
				//that is, if length > remaining(), then no bytes are transferred and a BufferUnderflowException is thrown.
//				buffer.asIntBuffer().get(tupleArr,offset,(int)len/4);
//				buffer.asIntBuffer().get(tupleArr,offset,Num_Attributes);
				buffer.getInt(offset);
				//next position in buffer to start
				offset += (int) 4*Num_Attributes;
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

	public Tuple read() throws IOException {
		while (!endOfFile) {
			// read a new page into the buffer and set the metadata accordingly
			if (needNewPage) {
				try {
					fetchPage();
				} catch (EOFException e) {
					break;
				}
				//System.out.println("============ page " + offsets.size() 
					//	+ "=== tuple " + numOfTuples +" =======");
			}
			
			if (buffer.hasRemaining()) {
				int [] cols = new int[numOfAttr];
				for (int i = 0; i < numOfAttr; i++) {
					cols[i] = buffer.getInt();
				}
				currTupleIdx++;
				return new Tuple(cols);
			}
			
			// does not has remaining
			eraseBuffer();
			needNewPage = true;		
		}
		
		return null;	// if reached the end of the file, return null
	}

	@Override
	//ReadNextTuple returns the next tuple line from the table file at the current "buffer position"
	public String ReadNextTuple() {
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
