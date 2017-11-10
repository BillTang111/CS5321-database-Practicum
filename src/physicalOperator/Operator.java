package physicalOperator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import Database_Catalog.Catalog;
import Tuple.Tuple;
import net.sf.jsqlparser.statement.select.PlainSelect;
import visitor.printQueryPlanVisitor;

/**
 * This class is the abstract operator class
 * 
 * @author Hao Rong, hr335
 */
public abstract class Operator implements Iterator {

	/** This method return the satisfied tuple and get next tuple from the child operator.
	 * @return the next tuple 
	 * @throws IOException 
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
	public abstract void dump(int printOrNot);
	//To Do
			// This method repeatedly calls getNextTuple() until the next tuple is null (no more output) 
			//and writes each tuple to a suitable PrintStream. That way you can dump() the results of any operator 
			//- including the root of your query plan - to your favorite PrintStream, 
			//whether it leads to a file or whether it is System.out.


	/** Get all the result tuple in this operator (For debugging) 
	 * @return a list of tuple
	 */
	public abstract ArrayList<Tuple> getAllTuple();
	
	/**
	 * Reset state to a specified index, only need to be implemented in sort operators.
	 */
	public abstract void reset(int index);
	
	/**
	 * Get current tuple index, only need to be implemented in sort operators.
	 */
	public abstract int getIndex();

	public abstract void accept(printQueryPlanVisitor printQueryPlanVisitor);

	
	
}
