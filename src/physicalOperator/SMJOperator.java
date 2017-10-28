package physicalOperator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import Tuple.Tuple;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import visitor.JoinAttributeVisitor;

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
	private EqulJoinTupleComparator joinCompare;
	private EqulJoinTupleComparator leftCompare;
	private EqulJoinTupleComparator rightCompare;

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
		Tuple Tleft= lastLeftTuple != null ? lastLeftTuple : (lastLeftTuple=leftOp.getNextTuple());
		Tuple Tright = rightOp.getNextTuple();
		if(currentPartiTuple!=null) {
			if(Tright!=null && rightCompare.compare(Tright,currentPartiTuple)==0) {
				lastLeftTuple = Tleft;

				return combineTuples(Tleft,Tright);
			}else {
				Tleft=leftOp.getNextTuple();
				if(Tleft==null) return null;
				if(leftCompare.compare(Tleft, lastLeftTuple)==0) {
					rightOp.reset(lastPartiIndex);
					Tright=rightOp.getNextTuple();
					lastLeftTuple=Tleft;
					return combineTuples(Tleft,Tright);
				}else {
					lastLeftTuple=null;
				}
			}
		}
		//start current partition on the right table's attributes
		currentPartiTuple=Tright;
		lastPartiIndex=rightOp.getIndex()-1;
		while(Tleft!=null&&currentPartiTuple!=null) {
			while(joinCompare.compare(Tleft, currentPartiTuple)<0) {
				Tleft=leftOp.getNextTuple();
			}
			while(joinCompare.compare(Tleft, currentPartiTuple)>0) {
				currentPartiTuple = rightOp.getNextTuple();
				lastPartiIndex++;
			}
			Tright=currentPartiTuple;
			if(joinCompare.compare(Tleft, Tright)>0) {
				lastLeftTuple=Tleft;
				return combineTuples(Tleft,Tright);
			}
		}
				return null;
	}

	// combines two tuples into one tuple
	public Tuple combineTuples(Tuple a, Tuple b) {

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
		Tuple tt = new Tuple(s, l);

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

}

/**
 * This class compares two tuples from two tables which are equal joined on the
 * join attribute.
 */

class EqulJoinTupleComparator implements Comparator<Tuple> {
	private List<Column> leftAttr;
	private List<Column> rightAttr;

	// constructor of the class creates a comparator
	public EqulJoinTupleComparator(List<Column> joinAttLeft, List<Column> joinAttRight) {
		this.leftAttr = joinAttLeft;
		this.rightAttr = joinAttRight;
	}

	@Override
	public int compare(Tuple t1, Tuple t2) {
		// TODO Auto-generated method stub

		for (int i = 0; i < leftAttr.size(); i++) {
			Column leftC = leftAttr.get(i);
			Column rightC = rightAttr.get(i);
			int vL = (int) t1.getTupleMap().get(leftC);
			int vR = (int) t1.getTupleMap().get(rightC);
			if (vL != vR) {
				return vL - vR;
			}
		}

		return 0;
	}

}
