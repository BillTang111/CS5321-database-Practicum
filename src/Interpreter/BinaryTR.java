package Interpreter;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
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
	private File input;			//file name
	
	private FileChannel fc;
	private int index = 0;
	private FileInputStream fin;
    private Queue<String> records = new LinkedList<>();
    private int pageId = -1;
	private int tupleId = -1;

    /**constructor*/
    public BinaryTR(File file) throws IOException  {
    	input = file;
    	String filePath = file.toString();
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
	                pageId += 1;
					tupleId = -1;
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
	        tupleId += 1;
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
            fin = new FileInputStream(input);
            fc = fin.getChannel();
            buffer = ByteBuffer.allocate(buffer_size);
        } catch (IOException e) {
            e.printStackTrace();
        }

	}

//reset the reader to a specific page and tuple index.
	public void reset (int pageNum, int tupleId) throws IOException {
		fc.position(pageNum*buffer_size);
		buffer = ByteBuffer.allocate(buffer_size);
		buffer.clear();
		for (int i=0; i<tupleId;i++) {
			this.ReadNextTuple();
		}
	}

	@Override
	public void dump() {
		// TODO Auto-generated method stub
		
	}
	
    /**
     * Read the next tuple from binary file 
     * without changing the elements in the record queue.
     * @return the next tuple
     */
    public String peek(){
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
		record = records.peek();
		record = record.substring(0, record.length() - 1);
        return record;	
    }
    
    
    /**
     * delete the file that is being read
     * **/
    public void deleteFile(){	
    	input.delete();
    }


    /**
     * Set the tuple reader at specified index
     */
	public void reset(int index2) {
		// TODO Auto-generated method stub
		fc = fin.getChannel();
    	this.records.clear();
    	int maxPerPage = 1022 / this.Num_Attributes;
    	int pageNum = index2 / maxPerPage;
    	int tupleIdxOfPage = index2 - pageNum * maxPerPage;
    	try {
			fc.position((long)(pageNum*buffer_size));
		} catch (IOException e) {
			e.printStackTrace();
		}
    	buffer = ByteBuffer.allocate(buffer_size);
    	buffer.clear();
    	for(int i=0; i<tupleIdxOfPage; i++){
    		this.ReadNextTuple();
    	}
	}
	
	/**
	 * Get page ID for this binary file.
	 * @return pageId
     */
	public int getPageId() {
		return pageId;
	}

	/**
	 * Get tuple ID for this binary file.
	 * @return tupleId
     */
	public int getTupleId() {
		return tupleId;
	}
}
