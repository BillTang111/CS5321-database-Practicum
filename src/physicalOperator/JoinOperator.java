package physicalOperator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Database_Catalog.Catalog;
import Tuple.Tuple;
import net.sf.jsqlparser.statement.select.PlainSelect;
import visitor.printLogicalQueryPlanVisitor;
import visitor.printPhysicalQueryPlanVisitor;
import visitor.visitor;
import net.sf.jsqlparser.expression.Expression;

/**
 * This class is used when sql query contains join condition.
 * 
 * @author Lini Tan, lt398
 */
public class JoinOperator extends Operator {
	
	Expression expression;
	Operator outter;
	Operator inner;
	Tuple a;
	
	public JoinOperator(Operator left, Operator right, Expression joinExpression) {
		// TODO Auto-generated constructor stub
		expression = joinExpression;
		outter = left;
		inner = right;
		a = outter.getNextTuple();
	}
	
	/** This method return the satisfied tuple and get next tuple from the child operator.
	 * @return the next tuple 
	 * */
	@Override
	public Tuple getNextTuple() {
		// TODO Auto-generated method stub
		while(a !=null){
			//System.out.println("hehe");
			//System.out.println("outter "+a.getTuple().toString());
			Tuple b = inner.getNextTuple();
			//System.out.println(b.getTuple().toString());
			
			while(b!=null){
				//System.out.println("inner1 "+b.getTuple().toString());
				ArrayList alist = a.getTuple();
				ArrayList blist = b.getTuple();
				String s = "";
				for(int i=0; i<alist.size(); i++){
					s=s+alist.get(i)+",";
				}
				for(int j=0; j<blist.size(); j++){
					s=s+blist.get(j)+",";
				}
				s = s.substring(0, s.length()-1);
				ArrayList l = new ArrayList();
				List aa = a.getNameList();
				List bb = b.getNameList();
				for(int i =0; i<aa.size(); i++){
					//if (l.contains(aa.get(i))) continue;
					l.add(aa.get(i));
				}
				for(int i =0; i<bb.size(); i++){
					if (l.contains(bb.get(i))) continue;
					l.add(bb.get(i));
				}
				Tuple tt = new Tuple(s,l);
				//System.out.println(tt.getTuple().toString());
				// build new visitor
				visitor v = new visitor(tt);
				expression.accept(v);
				if(v.getResult()){
					//System.out.println("right: "+tt.getTuple().toString());
					return tt;
				}
				b = inner.getNextTuple();
				//System.out.println(b.getTuple().toString());
				
			}
//inner is null
			inner.reset();
			a = outter.getNextTuple();
			//System.out.println("hh");
			//System.out.println("hh"+a.getTuple().toString());
			//System.out.println("hh");
//			b = inner.getNextTuple();
//			System.out.println(b.getTuple().toString());
		}
		return null;
	}

	/**Reset the operator to re-call from the beginning */
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		inner.reset();
		outter.reset();		
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
	
	
	public Operator getOutterChild(){
		return this.outter;
	}
	
	public Operator getInnerChild(){
		return this.inner;
	}

	
	@Override
	public void accept(
			printPhysicalQueryPlanVisitor printPhysicalQueryPlanVisitor) {
		printPhysicalQueryPlanVisitor.visit(this);
	}

	public String getConditionString() {
		return expression.toString();
	}

}
