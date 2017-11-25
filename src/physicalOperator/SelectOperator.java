package physicalOperator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import Database_Catalog.Catalog;
import Tuple.Tuple;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.PlainSelect;
import visitor.printLogicalQueryPlanVisitor;
import visitor.printPhysicalQueryPlanVisitor;
import visitor.visitor;

/**
 * This class is used when sql query contains select condition.
 * 
 * @author Lini Tan, lt398
 */
public class SelectOperator extends Operator {
	
//	PlainSelect select; //Store the plainSelect object parsed from query
	Expression e;
//	BufferedReader br;
//	FromItem input;
//	String location;
	Operator childOp;
	
	public SelectOperator(Expression singleSelect, Operator op) throws IOException {
		// TODO Auto-generated constructor stub
//		input = selectBody.getFromItem();
//		location = Catalog.getInstance().getInputLocation();	
//		br = new BufferedReader(new FileReader(location + "/db/data/"+input));  
//		select = selectBody;
//		e = selectBody.getWhere();
		e = singleSelect;
		childOp = op;
	}

	/** This method return the satisfied tuple and get next tuple from the child operator.
	 * @return the next tuple 
	 * */
	@Override
	public Tuple getNextTuple() {
		Tuple a = childOp.getNextTuple();
		while(a!=null){
			visitor v = new visitor(a);
//			if(e==null){ 
//				System.out.println("condition is null");
//				return a;
//				}
			e.accept(v);
			if(v.getResult()){
				return a;
			}
			a = childOp.getNextTuple();
		}
		return null;
	}	

	/**Reset the operator to re-call from the beginning */
	@Override
	public void reset() {
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
		while(a != null){
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

	public Operator getChild() {
		return this.childOp;
	}

	@Override
	public void accept(
			printPhysicalQueryPlanVisitor printPhysicalQueryPlanVisitor) {
		printPhysicalQueryPlanVisitor.visit(this);
	}



}
