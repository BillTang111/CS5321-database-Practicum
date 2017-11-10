package physicalOperator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import Database_Catalog.Catalog;
import Tuple.Tuple;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import visitor.JoinAttributeVisitor;
import visitor.printQueryPlanVisitor;

/**
 * @author benzhangtang
 *
 *         Sort Merge join OPerator is a join operator implemented useing Sort
 *         Merge Join Algorithm. NOTE: 1.the join order follows the order in the
 *         FROM clause; 2. SM join is only good for equal join
 *
 *         Example: the query SELECT * FROM R, S, T WHERE R.A = T.B AND S.C =
 *         T.K is not a query we will test with, because the join order in the
 *         FROM clause. The first join is a cross product of R and S.
 */

public class SMJOperator extends Operator {

	private Operator leftOp;
	private Operator rightOp;
	private Expression joinExpr;
	private Tuple lastLeftTuple;
	private Tuple currentPartiTuple;
	private int lastPartiIndex;
	private Comparator<Tuple> joinCompare;
	private Comparator<Tuple>  leftCompare;
	private Comparator<Tuple>  rightCompare;

	public SMJOperator(Operator leftOp, Operator rightOp, Expression joinExpr) {
		this.leftOp = leftOp;
		this.rightOp = rightOp;
		this.joinExpr = joinExpr;
		JoinAttributeVisitor visitor = new JoinAttributeVisitor();
		this.joinExpr.accept(visitor);
		this.lastLeftTuple = null;
		this.currentPartiTuple = null;
		this.lastPartiIndex = 0;
		this.joinCompare = new EqulJoinTupleComparator(visitor.getLeftAttr(), visitor.getRightAttr());
		this.leftCompare = new EqulJoinTupleComparator(visitor.getLeftAttr(), visitor.getLeftAttr());
		this.rightCompare = new EqulJoinTupleComparator(visitor.getRightAttr(), visitor.getRightAttr());
	}

	// get method to get the next SM joined tuple
	@Override
	public Tuple getNextTuple() {
		// TODO Auto-generated method stub
//		Tuple Tleft = lastLeftTuple;
//		if(Tleft != null){
//			return lastLeftTuple;
//		}else{
//			lastLeftTuple=leftOp.getNextTuple();
//		}
//		Tuple Tleft = null;
//		if (lastLeftTuple != null){
//			Tleft=lastLeftTuple;
//		}else{
//			lastLeftTuple=leftOp.getNextTuple();
//		}
		
		Tuple Tleft= lastLeftTuple != null ? lastLeftTuple : (lastLeftTuple=leftOp.getNextTuple());
		Tuple Tright = rightOp.getNextTuple();
		if(currentPartiTuple!=null) {
			if(Tright!=null && rightCompare.compare(Tright,currentPartiTuple)==0) {
				lastLeftTuple = Tleft;
				//System.out.println("new Tuple: "+combineTuples(Tleft,Tright).getTuple().toString());
				return combineTuples(Tleft,Tright);
			}else {
				Tleft=leftOp.getNextTuple();
				if(Tleft==null) return null;
				if(leftCompare.compare(Tleft, lastLeftTuple)==0) {
					//System.out.println("last Part index: "+this.lastPartiIndex);
					rightOp.reset(this.lastPartiIndex);
					
					Tright=rightOp.getNextTuple();
					//System.out.println("Last Part" + Tright.getTuple().toString());
					lastLeftTuple=Tleft;
					return combineTuples(Tleft,Tright);
				}else {
					lastLeftTuple=null;
				}
			}
		}
		//start current partition on the right table's attributes
		currentPartiTuple=Tright;
		
		this.lastPartiIndex=rightOp.getIndex()-1;
		//System.out.println("last Part index now: "+lastPartiIndex);
		while(Tleft!=null&&currentPartiTuple!=null) {
			while(joinCompare.compare(Tleft, currentPartiTuple)<0) {
				Tleft=leftOp.getNextTuple();
			}
			while(joinCompare.compare(Tleft, currentPartiTuple)>0) {
				currentPartiTuple = rightOp.getNextTuple();
				this.lastPartiIndex++;
			}
			Tright=currentPartiTuple;
			if(joinCompare.compare(Tleft, Tright)==0) {
				lastLeftTuple=Tleft;
				return combineTuples(Tleft,Tright);
			}
		}
				return null;
	}

	/** combines two tuples into one tuple */
	public Tuple combineTuples(Tuple a, Tuple b) {
		if(a==null || b== null) return null;
		ArrayList alist = a.getTuple();
		ArrayList blist = b.getTuple();
		String s = "";
		for (int i = 0; i < alist.size(); i++) {
			s = s + alist.get(i) + ",";
		}
		for (int j = 0; j < blist.size(); j++) {
			s = s + blist.get(j) + ",";
		}
		s = s.substring(0, s.length() - 1);
		ArrayList l = new ArrayList();
		List aa = a.getNameList();
		List bb = b.getNameList();
		for (int i = 0; i < aa.size(); i++) {
			// if (l.contains(aa.get(i))) continue;
			l.add(aa.get(i));
		}
		for (int i = 0; i < bb.size(); i++) {
			if (l.contains(bb.get(i)))
				continue;
			l.add(bb.get(i));
		}
		//System.out.println("new filds: "+l.toString());
		Tuple tt = new Tuple(s, l);
		//System.out.println("new tuple: "+tt.getTuple().toString());

		return tt;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dump(int printOrNot) {
		// TODO Auto-generated method stub

	}

	@Override
	public ArrayList<Tuple> getAllTuple() {
		// TODO Auto-generated method stub
		return null;
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

	@Override
	public void accept(printQueryPlanVisitor printQueryPlanVisitor) {
		printQueryPlanVisitor.visit(this);
	}
	
	public Operator getOutterChild(){
		return this.leftOp;
	}
	
	public Operator getInnerChild(){
		return this.rightOp;
	}
}

/**
 * This class compares two tuples from two tables which are equal joined on the
 * join attribute.
 */

class EqulJoinTupleComparator implements Comparator<Tuple> {
	private List leftAttr;
	private List rightAttr;
	private HashMap<String,String> getPairAlias;

	// constructor of the class creates a comparator
	public EqulJoinTupleComparator(List joinAttLeft, List joinAttRight) {
		this.leftAttr = joinAttLeft;
		this.rightAttr = joinAttRight;
		Catalog cata = Catalog.getInstance();
		getPairAlias = cata.getPairAlias();
	}

	/** Return 0 when t1=t2, return difference(t1.cValue-t2.cValue) when t1!=t2.*/
	@Override
	public int compare(Tuple t1, Tuple t2) {
		ArrayList t1List = t1.getTuple();
		//System.out.println(t1List.toString());
		ArrayList t2List = t2.getTuple();
		//System.out.println(t2List.toString());
		if(t1==null||t2==null){
			System.out.println("---");
		}
		
		for (int i = 0; i < leftAttr.size(); i++) {
			String leftC = (String) leftAttr.get(i);
			String rightC = (String) rightAttr.get(i);
			int leftDotInx = leftC.indexOf(".");
			int rightDotInx = rightC.indexOf(".");
			
			leftC = getPairAlias.get(leftC.substring(0, leftDotInx))+"."+leftC.substring(leftDotInx+1, leftC.length());
			rightC = getPairAlias.get(rightC.substring(0, rightDotInx))+"."+rightC.substring(rightDotInx+1, rightC.length());
			
			//System.out.println("I am left index: "+leftC);
			//System.out.println(t1.getTupleMap());
			int indexL = (int) t1.getTupleMap().get(leftC);
			//System.out.println("I am left index: "+);
			int indexR = (int) t2.getTupleMap().get(rightC);
			int vL = Integer.parseInt((String) t1List.get(indexL)) ;
			//System.out.println("I am left index: "+vL);
			int vR = Integer.parseInt((String) t2List.get(indexR)) ;
			//System.out.println("I am right index: "+vR);
			if (vL != vR) {
				return vL - vR;
			}
		}

		return 0;
	}

}
