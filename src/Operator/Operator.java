package Operator;

import java.util.Iterator;

import Tuple.Tuple;

public abstract class Operator implements Iterator {
	
	public void dump() {
		//To Do
		// This method repeatedly calls getNextTuple() until the next tuple is null (no more output) 
		//and writes each tuple to a suitable PrintStream. That way you can dump() the results of any operator 
		//- including the root of your query plan - to your favorite PrintStream, 
		//whether it leads to a file or whether it is System.out.
		
	}
	
	
	public Tuple getNextTuple() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void reset() {
		// TODO Auto-generated method stub
	}
	
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
}
