package Operator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import Database_Catalog.Catalog;
import Tuple.Tuple;
import net.sf.jsqlparser.statement.select.PlainSelect;

/**
 * This class is the abstract operator class
 * 
 * @author Hao Rong, hr335
 */
public abstract class Operator implements Iterator {

	/** This method return the satisfied tuple and get next tuple from the child operator.
	 * @return the next tuple 
	 * */
	public abstract Tuple getNextTuple();
	
	/**Reset the operator to re-call from the beginning */
	public abstract void reset();

	/**@return a boolean whether it has next */
	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object next() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**To print your result. Use for debug */
	public abstract void dump();
	//To Do
			// This method repeatedly calls getNextTuple() until the next tuple is null (no more output) 
			//and writes each tuple to a suitable PrintStream. That way you can dump() the results of any operator 
			//- including the root of your query plan - to your favorite PrintStream, 
			//whether it leads to a file or whether it is System.out.

	/**Write the tuple to the file
	 * @return a list of tuple
	 */
	public ArrayList<Tuple> writeToFile() {
		// TODO Auto-generated method stub
		return null;
	}
}
