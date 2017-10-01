package Operator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;
import Database_Catalog.Catalog;
import Tuple.Tuple;
import net.sf.jsqlparser.statement.select.PlainSelect;

public abstract class Operator implements Iterator {

	public abstract Tuple getNextTuple();
	
	public abstract void reset();

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
	
	public abstract void writeToFile(BufferedWriter bw) throws IOException;
	
	public abstract void dump();
	//To Do
			// This method repeatedly calls getNextTuple() until the next tuple is null (no more output) 
			//and writes each tuple to a suitable PrintStream. That way you can dump() the results of any operator 
			//- including the root of your query plan - to your favorite PrintStream, 
			//whether it leads to a file or whether it is System.out.
}
