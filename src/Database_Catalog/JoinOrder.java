package Database_Catalog;

import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;

/**
 * class [JoinOder] uses Dynamic Programming algorithm to determine the best fit 
 * join order with lowest I/O cost.
 * This class takes the logical join operator from the logical join plan in [LogicalQueryPlan] as input,
 * transforming such join operator with multiply children into a left-deep tree. 
 * 
 * @author benzhangtang
 */
public class JoinOrder {

	private List<String> tables = new ArrayList<>(); //list of relations to be joined
	private List<Expression> expressions = new ArrayList<>();//list of expressions 
	
	
//	public JoinOrder()
	
	/*
	 * [calc_V_value] calculates the V-value for an attribute [column c] which takes
	 * in a table t. 
	 * Require: table t is either a base table, or a after-selection table
	 * @param: a column (ie. R.a)
	 * @return: corresponding V_value
	 */
	private double calc_V_value(Column c) {
		return 0;
		
	}
	
	
	
	/*
	 * [calc_JoinCost] is a helper function that returns the 
	 * the cost (expected size) after the join. 
	 * @param: List of tables to be joined 
	 */
	private double calc_JoinCost (List<String> tableList) {
		return 0;
		
	}
	
	/*[pd_Table_order] is a method that uses dynamic programming algo 
	 * to determine the cost of different joins.
	 */
	private void dp_Table_order() {
		int N = tables.size(); // numb of relations
		OrderStorage [] [] OC_Table = new OrderStorage [N] [N];
		
		//loop to setup the first row of OC_Table 
		for (int i=0; i<N; i++) {
			List<String> tableName = new ArrayList<>();
			tableName.add(tables.get(i));
			List<Expression> expressionName = new ArrayList<>();
			expressionName.add(expressions.get(i));
			OC_Table [0] [i] = new OrderStorage (tableName,expressionName, 0); 
		}
		
		
	}
	
	
	
	
	/**
	 * @author benzhangtang
	 *
	 * class [OrderStorage] is a helper class for [JoinOrder] class. 
	 * This class stores the one possibility of join order as a list, 
	 * and the associating cost with this order.
	 */
	private class OrderStorage {
		private List<String> tableList = new ArrayList<>();
		private List<Expression> expressionList = new ArrayList<>();
		private int cost;
		
		
		public OrderStorage (List<String> tableList, List<Expression> expList, int cost) {
			this.tableList = tableList;
			this.expressionList = expList;
			this.cost = cost;
		}
		
		public List<String> getTableList() {
			return tableList;
		}
		
		public List<Expression> getExpressionList(){
			return expressionList;
		}
		
		public int getScore() {
			return cost;
		}
	}
	
	
}


