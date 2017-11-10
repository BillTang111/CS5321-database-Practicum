package physicalOperator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import Database_Catalog.Catalog;
import Tuple.Tuple;
import net.sf.jsqlparser.statement.select.PlainSelect;
import visitor.printQueryPlanVisitor;

/**
 * This class is used when sql query contains "distinct". It is used to remove duplicate 
 * tuple. 
 * 
 * @author Lini Tan, lt398
 */
public class DuplicateEliminationOperators extends Operator {
	
	private Operator childOp;
	//HashSet distinctTuple;
	private Tuple last; 
	
	public DuplicateEliminationOperators(Operator op){
		childOp = op;
		last = null;
		//distinctTuple = new HashSet();
		
	}
	
	/** This method remove the duplicate tuple and return the non-duplicate one.
	 * @return the next tuple 
	 * */
	@Override
	public Tuple getNextTuple() {
		// TODO Auto-generated method stub
//		Tuple a = childOp.getNextTuple();
//		while(a!=null){
//		if(!distinctTuple.contains(a.getTuple().toString())){
//			distinctTuple.add(a.getTuple().toString());
//			return a;
//		}
//		a=childOp.getNextTuple();
//		}
//		return null;
		
		Tuple a = childOp.getNextTuple();
		while(a != null){
			if(last == null || !last.getTuple().toString().equals(a.getTuple().toString())){
				last = a;
				return a;
			}
			a = childOp.getNextTuple();
		}
		return null;
	}
	
	/**Reset the operator to re-call from the beginning */
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		last = null;
		childOp.reset();
	}
	
	/**To print your result. Use for debug 
	 * @param printOrNot: 0: don't print, 1: print*/
	@Override
	public void dump(int printOrNot) {
		// TODO Auto-generated method stub
		Tuple a =getNextTuple();
		if (printOrNot==1){
			while(a != null){
				System.out.println(a.getTuple());
				a=getNextTuple();
				//System.out.println(a.toString());
			}
		} 
		else if (printOrNot==0){
			while(a != null){
				a=getNextTuple();
			}
		}
	}
	
	/** Get all the result tuple in this operator (For debugging) 
	 * @return a list of tuple
	 */
	@Override
	public ArrayList<Tuple> getAllTuple() {
		// TODO Auto-generated method stub
		Tuple a =getNextTuple();
		ArrayList<Tuple> result = new ArrayList<Tuple>();
		while(a!= null){
			result.add(a);
			a =getNextTuple();
		}
		return result;
	}

	@Override
	public void reset(int index) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getIndex() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public Operator getChild(){
		return this.childOp;
	}
	
	@Override
	public void accept(printQueryPlanVisitor printQueryPlanVisitor) {
		printQueryPlanVisitor.visit(this);
	}
}
