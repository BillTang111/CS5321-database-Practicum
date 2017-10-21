package physicalOperator;

import java.util.ArrayList;
import java.util.List;

import Tuple.Tuple;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.PlainSelect;

/**
 * This class is used to sort externally.
 * Only used in Sort-Merge Join.
 * 
 * @author Hao Rong, hr335
 */
public class ExternalSortOperator extends Operator{
	
	int bufferSize;
	String jSortField; // the column name in the join condition
	
	public ExternalSortOperator(Operator op, int Size, Expression joinCondition){
		bufferSize = Size;
		
		
		
		Tuple sample = op.getNextTuple();
		Object[] mapKeySet = sample.getTupleMap().keySet().toArray();
		List order = new ArrayList();

		jSortField = Column;
	}

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
