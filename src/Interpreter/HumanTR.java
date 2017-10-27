package Interpreter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import Tuple.Tuple;

/**
 * @author benzhang tang
 * HumanTR class class offers an efficient way to read tuples from a normal character file of tuples
 * Feature: Buffer reader is used and the file is read line by line. 
 * A tuple is a line of ints separated by commas 
 */
public class HumanTR implements TupleReader{
	private File file;  //file of table to be read
	private BufferedReader br; // A  buffered reader for the file
	private String tableName;
	
	/**
	 * constructor for the class returns the object containing file and and buffered reader for the file
	 * @param file
	 * @throws FileNotFoundException
	 */
	public HumanTR (File file) throws FileNotFoundException {
		this.file=file;
		br = new BufferedReader(new FileReader(file));
		String filePath = file.toString();
    	int start = filePath.lastIndexOf("/")+1;
    	tableName = filePath.substring(start);
	}

	@Override
	/**
	 * ReadNextTuple method returns the next "tuple" in form of string in the file if there is one; 
	 * it returns null if eof.
	 */
	public String ReadNextTuple() throws IOException {
		String line = br.readLine();
		if (line == null) return null;
		return line;
	}
	
	/**
	 * ReadNextTuple method returns the next "tuple" in form of string in the file if there is one; 
	 * it returns null if eof.
	 */
	public Tuple ReadNextTuple2() throws IOException {
		String line = br.readLine();
		if (line == null) return null;
		ArrayList TableName = new ArrayList();
        TableName.add(tableName);
        return new Tuple(line,TableName);
	}

	
	// close the reader
	public void close() {
		try {
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	@Override
	public void reset() throws IOException {
		if (br != null) {
			br.close();
		}
		br = new BufferedReader(new FileReader(file));
	}

	@Override
	public void dump() {
		// TODO Auto-generated method stub
		
	}
	
    /**
     * Read the next tuple from readable file 
     * without changing the elements in the buffered reader.
     * @return the next tuple
     */
    public Tuple peek(){
    	try {
    		br.mark(1000);
            String record = br.readLine();
            br.reset();
            if(record == null) {
                return null;
            }
            ArrayList TableName = new ArrayList();
            TableName.add(tableName);
            return new Tuple(record,TableName);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * delete the file that is being read
     * and close the buffer reader
     * **/
    public void deleteFile(){	
  
    	file.delete();
    }

    /**
     * Set the tuple reader at specified index
     */
	public void reset(int index) {
		// TODO Auto-generated method stub
		
	}

	
}
