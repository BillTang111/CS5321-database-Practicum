package Operator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import Database_Catalog.Catalog;
import Tuple.Tuple;
import net.sf.jsqlparser.statement.select.PlainSelect;

/**
 * This class is used when sql query contains "distinct". It is used to remove duplicate 
 * tuple. 
 * 
 * @author Lini Tan, lt398
 */
public class DuplicateEliminationOperators extends Operator {
	
	Operator childOp;
	HashSet distinctTuple;
	
	public DuplicateEliminationOperators(Operator op){
		childOp = op;
		distinctTuple = new HashSet();
		
	}
	
	/** This method remove the duplicate tuple and return the non-duplicate one.
	 * @return the next tuple 
	 * */
	@Override
	public Tuple getNextTuple() {
		// TODO Auto-generated method stub
		Tuple a = childOp.getNextTuple();
		while(a!=null){
		//if(distinctTuple.isEmpty()) distinctTuple.add(a);
		if(!distinctTuple.contains(a.getTuple().toString())){
			distinctTuple.add(a.getTuple().toString());
			return a;
		}
		a=childOp.getNextTuple();
		}
		return null;
	}
	
	/**Reset the operator to re-call from the beginning */
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		childOp.reset();
	}
	
	/**To print your result. Use for debug */
	@Override
	public void dump() {
		// TODO Auto-generated method stub
		Tuple a =getNextTuple();
		while(a != null){
			System.out.println(a.getTuple());
			a =getNextTuple();
		}
	}
	
	/**Write the tuple to the file
	 * @return a list of tuple
	 */
	@Override
	public ArrayList<Tuple> writeToFile() {
		// TODO Auto-generated method stub
		Tuple a =getNextTuple();
		ArrayList<Tuple> result = new ArrayList<Tuple>();
		while(a!= null){
			result.add(a);
			a =getNextTuple();
		}
		return result;
	}
}
