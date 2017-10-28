package physicalOperator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import Tuple.Tuple;
import net.sf.jsqlparser.schema.Column;

/**
 * @author benzhangtang
 *
 *Sort Merge join OPerator is a join operator implemented useing Sort Merge Join Algorithm. 
 *NOTE: 1.the join order follows the order in the FROM clause;  
 *		2. SM join is only good for equal join
 *
 * Example: the query SELECT * FROM R, S, T WHERE R.A = T.B AND S.C = T.K 
 * is not a query we will test with, because the join order in the FROM clause.
 * The first join is a cross product of R and S.  
 */

public class SMJOperator extends Operator {

	@Override
	public Tuple getNextTuple() {
		// TODO Auto-generated method stub
		return null;
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

}



/**
 * This class compares two tuples from two tables which are equal joined 
 * on the join attribute. 
 */

class EqulJoinTupleComparator implements Comparator<Tuple>{
	private List<Column> leftAttr;
	private List<Column> rightAttr;

	// constructor of the class creates a comparator 
	public EqulJoinTupleComparator(List<Column> joinAttLeft, List<Column> joinAttRight) {
		this.leftAttr=joinAttLeft;
		this.rightAttr=joinAttRight;
	}
	
	@Override
	public int compare(Tuple t1, Tuple t2) {
		// TODO Auto-generated method stub
		
		
		return 0;
	}
	
}


