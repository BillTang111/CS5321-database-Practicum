package Interpreter;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import Tuple.Tuple;
import net.sf.jsqlparser.schema.Column;

/**
 * @author benzhang tang
 * 
 * BinaryTR class offers an efficient way to read tuples from a binary file of tuples 
 * File format: input file is a sequence of pages of (4*1024) bytes. # of attributes and # of tuples on the page
 * are two metadata stored in each page. Each field of the tuple is stored as 4 bytes integer. 
 * Feature: Java NIO is used in this class to increase the speed of I/O
 */
public class BinaryTR implements TupleReader {

	int Num_Attributes =0;            // number of attributes in a tuple
	int TupleNum_on_page =0;		  // number of tuples in a page
	private static final int buffer_size = 4*1024; // size of the buffer page
	private ByteBuffer buffer;     //buffer page
	private File file;			//file name
	
	private FileChannel fc;
	private int index = 0;
	private FileInputStream fin;
    private Queue<String> records = new LinkedList<>();

    /**constructor*/
    public BinaryTR(File file) throws IOException  {
    	File input = file;
    	try {
            fin = new FileInputStream(input);
            fc = fin.getChannel();
            buffer = ByteBuffer.allocate(buffer_size);
        } catch (IOException e) {
            e.printStackTrace();
        }
    } 



	public String ReadNextTuple() {
		// TODO Auto-generated method stub
		 String record = "";
	        if (records.isEmpty()) {
	            try {
	                buffer.clear();
	                int r = fc.read(buffer);
	                if (r == -1) {
	                    return null;
	                }
	                index = 0;
	                Num_Attributes = buffer.getInt(index);
	                TupleNum_on_page = buffer.getInt(index + 4);
	                index += 8;
	                for (int i = 0; i < TupleNum_on_page; i++) {
	                    for (int j = 0; j < Num_Attributes; j++) {
	                        int value = buffer.getInt(index);
	                        record += Integer.toString(value) + ",";
	                        index += 4;
	                    }
	                    records.add(record);
	                    record = "";
	                }
	                index = 8;
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        record = records.poll();
	        index += 4;
	        record = record.substring(0, record.length() - 1);
		return record;
	}

//	@Override
//	public void close() {
//		// TODO Auto-generated method stub
//
//	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		try {
            fin.close();
            FileInputStream fin = new FileInputStream(file);
            fc = fin.getChannel();
            buffer = ByteBuffer.allocate(buffer_size);
        } catch (IOException e) {
            e.printStackTrace();
        }

	}



	@Override
	public void dump() {
		// TODO Auto-generated method stub
		
	}
}
