package physicalOperator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Database_Catalog.Catalog;
import Tuple.Tuple;
import net.sf.jsqlparser.statement.select.PlainSelect;
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
			//System.out.println("outter "+a.getTuple().toString());
			Tuple b = inner.getNextTuple();
			
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
				// build new visitor
				visitor v = new visitor(tt);
				expression.accept(v);
				if(v.getResult()){
					return tt;
				}
				b = inner.getNextTuple();
			}
//inner is null
			inner.reset();
			a = outter.getNextTuple();
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

	/**To print your result. Use for debug */
	@Override
	public void dump() {
		// TODO Auto-generated method stub
		Tuple a =getNextTuple();
		while(a != null){
			System.out.println(a.getTuple());
			a=getNextTuple();
			//System.out.println(a.toString());
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
		while(a != null){
			result.add(a);
			a =getNextTuple();
		}
		return result;
	}

}
