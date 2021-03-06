package Database_Catalog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import UnionFind.Element;
import logicalOperator.LogicalOperator;
import logicalOperator.LogicalScanOperator;
import logicalOperator.LogicalSelectOperator;
import logicalOperator.LogicalUnionJoinOperator;
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
	private Map<String, Expression> Table_Expres_Map = new HashMap<>();
	private List<Element> union;
	private List<Integer> tablesIndex = new ArrayList<>();
	
	//Question: expression? TableIndex? getJoinSize-> second for loop
	
	
	/*
	 * constructor of this class
	 */
	public JoinOrder(LogicalUnionJoinOperator UnionJoinOp, List<Element> union) {
		List<LogicalOperator> LogOperators = UnionJoinOp.getChildrenOperators();
		this.union = union;
		//loop through operators to get each table name
        for (LogicalOperator op : LogOperators) {
            String init_table = "";
            Expression init_exp = null;
            if (op instanceof LogicalScanOperator) {
                init_table = ((LogicalScanOperator) op).getTableName();
            } else if (op instanceof LogicalSelectOperator) {
                init_table = ((LogicalScanOperator) ((LogicalSelectOperator) op).getchildOperator()).getTableName();
                init_exp = ((LogicalSelectOperator) op).getSelectCondition();
            }
            tables.add(init_table);
            expressions.add(init_exp);
            Table_Expres_Map.put(init_table, init_exp);
        }
        // use dp algo to sort tables
        dp_Table_order();
	}
	

	/*
	 * getter methods for this class
	 * return: list of tables with well arranged join order
	 */
	public List<String> getTables() {
		return tables;
	}
	
	/*
	 * getter methods for this class
	 * return: list of expressions
	 */
	public List<Expression> getExpressions() {
		return expressions;
	}
	
	/*
	 * getter methods for this class
	 * return: list of expressions
	 */
	public List<Integer> getTablesIndex(){
		return tablesIndex;
	}
	
	/*
	 * [calc_V_value] calculates the V-value for an attribute [column c] which takes
	 * in a table [Tname]. 
	 * Require: table t is either a base table, or a after-selection table
	 * @param: a column (ie. R.a)
	 * @return: corresponding V_value
	 */
	private double calc_V_value(Column c) {
		double V_value = 1;
		StatsInfo statsInfo = Catalog.getInstance().getStatsInfo();
		ArrayList<Integer> attri_stats = statsInfo.getFieldAndBound().get(c.toString());
		String Tname = c.getTable().getName();
		System.out.println("I am the table name "+ Tname);
		System.out.println("I am the column name "+ c.getColumnName());
		Expression e =Table_Expres_Map.get(Tname);
		System.out.println("I ame the table name: "+Tname);
		System.out.println("I am the column name:"+c.getColumnName());
		System.out.println(c.toString());
		String s = "Sailors.A";
		System.out.println(statsInfo.getFieldAndBound().containsKey(s));
		System.out.println((long) attri_stats.get(0));
		System.out.println((long) attri_stats.get(1));
		
		if(e==null) { 	//if there is no selection
			 V_value = attri_stats.get(1) - attri_stats.get(0) +1;
		} else {			//if there is a selection
			 V_value = statsInfo.getReductionFactorClosed
					 (Tname, c.getColumnName(), (long) attri_stats.get(0), (long) attri_stats.get(1));
		}
		return V_value;
	}
	
	
	/*
	 * [calc_JoinCost] is a helper function that returns the 
	 * the cost (expected size) after the join. 
	 * @param: List of tables to be joined 
	 */
	private double calc_JoinCost (List<String> tableList) {
		double Join_Cost = 1.0;
		List<String> tempTableList = new ArrayList<>();
		tempTableList.addAll(tableList);
		String LastT = tempTableList.remove(tempTableList.size() - 1);
		
		for (String t : tempTableList) {
			StatsInfo statsInfo = Catalog.getInstance().getStatsInfo();
			//System.out.println("String: " + t);
			if (t.contains("*")){
				t = t.substring(0, t.length()-1);
				//System.out.println("String: " + t);
			}
			//System.out.println("why null: " + statsInfo.getTableAndSizeMap());
			Join_Cost = Join_Cost * statsInfo.getTableAndSizeMap().get(t);
		}
		
		for (Element e: union) {
			Set<String> tableUnion = new HashSet<>();
			for (Column c1: e.getattri()) {
				String a1 = c1.getTable().getName();
				for (Column c2: e.getattri()) {
					String a2 = c2.getTable().getName();
					if (!tableUnion.contains(a1+a2)&&!tableUnion.contains(a2+a1)) {
						tableUnion.add(a1+a2); tableUnion.add(a2+a1);
						if (tableList.contains(a1)&&tableList.contains(a2)) {
							double v1 = calc_V_value(c1); double v2 = calc_V_value(c2);
							Join_Cost = Join_Cost / Math.max(v1, v2);
						}
					}
				}
			}
			if (Join_Cost< 1.0){
				Join_Cost =1.0;
			}
		}
		return Join_Cost;
	}
	
//	[minCost_Index] returns the index of the smallest cost in an array
	private int minCost_Index(double [] costLst) {
		int index=0; double minCost = Double.MAX_VALUE;
		for (int i=0; i<costLst.length; i++) {
			if (minCost > costLst [i]) {
				minCost = costLst [i];
				index = i;
			}
		}
		return index;
	}
	
	/*[pd_Table_order] is a method that uses dynamic programming algo 
	 * to determine the cost of different joins.
	 */
	private void dp_Table_order() {
		int N = tables.size(); // numb of relations
		OrderStorage [] [] OC_Table = new OrderStorage [N] [N]; //intermediate calc storage
		
		//loop to setup the first row of OC_Table 
		for (int i=0; i<N; i++) {
			List<String> tableName = new ArrayList<>();
			tableName.add(tables.get(i));
			List<Expression> expressionName = new ArrayList<>();
			expressionName.add(expressions.get(i));
			OC_Table [0] [i] = new OrderStorage (tableName,expressionName, 0); 
			System.out.println("i am getting table" + OC_Table[0][i].tableList);
		}
		
		for (int i=1; i<N; i++) {//not include the first row
			for (int j=0; j<N; j++) { //loop the columns
				//costs of current run
				double[] curCost = new double[N]; 
				//require the last [OrderStorage] to store the current values (tables, expres, cost)
				OrderStorage preStorage = OC_Table[i-1] [j];
				
				for(int k=0; k<N; k++) { //loop to add a new relation to the current relation
					String tableToAdd = tables.get(k);
					Expression expToAdd = expressions.get(k);
					List<String> PreTableList = new ArrayList<> (preStorage.getTableList());
					List<Expression> preExpList = new ArrayList<> (preStorage.getExpressionList());
					
					if(!PreTableList.contains(tableToAdd)) { //if a table has not been ordered
						PreTableList.add(tableToAdd);
						preExpList.add(expToAdd);
						curCost[k] = calc_JoinCost(PreTableList); //calc cost of current order
				} else {//table is ordered; diagonal in the matrix; set to a large value -> no effect
						curCost[k] = Double.MAX_VALUE;
						
					}
				}

				//after trying to add a table from a row of tables; gets the lowest cost & index
				int minCostIndex = minCost_Index (curCost);
				double newOrderCost =  curCost [minCostIndex] + preStorage.getScore();
				
				//get new best table order in the current row
				List<String> curTableNames = new ArrayList<> (preStorage.getTableList());
				curTableNames.add(tables.get(minCostIndex));
				
				//get new best expressions in the current row
				List<Expression> curExpList = new ArrayList<> (preStorage.getExpressionList());
				curExpList.add(expressions.get(minCostIndex));
				
				//create data storage in the [tables]'s [ith,jth] row and column
				System.out.println("the loop id is" + i +" and " + j);
				OC_Table [i] [j] = new OrderStorage (curTableNames,curExpList,newOrderCost);
				System.out.println("i am adding table in loop" + OC_Table[i][j].tableList);
				System.out.println("this order's cost is: " + OC_Table[i][j].cost);
			}
		}
		
		//get the best join order (lowest cost) from the last row of the matrix
		//1. retrieve the cost array of the last in the [tables]
		double [] lastRowCosts = new double [N]; 
		for (int j=0; j< N; j++) {
			lastRowCosts[j] = OC_Table [N-1] [j].getScore();
			System.out.println("last row order is: " + OC_Table [N-1] [j].tableList);
			System.out.println("last row cost is: " + lastRowCosts[j]);
		}
		

		
		//2. get the lowest cost order's index
		int lowestCostInx = minCost_Index(lastRowCosts);
		//3. get the best join order & update field
		tables = OC_Table [N-1] [lowestCostInx].getTableList();
		System.out.println("right order is: " + tables);
		expressions = OC_Table [N-1] [lowestCostInx].getExpressionList();
		
		//set tableIndex
        for (String t : OC_Table[N - 1][lowestCostInx].getTableList()) {
            tablesIndex.add(tables.indexOf(t));
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
		private double cost;
		
		/*
		 * constructor of [OrderStorage]
		 * @para: 
		 * [tableList] is a ordered list of tables
		 * [expList] is a list of expressions corresponding to tables
		 * [newOrderCost] is the cost of such join order
		 */
		public OrderStorage (List<String> tableList, List<Expression> expList, double newOrderCost) {
			this.tableList = tableList;
			this.expressionList = expList;
			this.cost = newOrderCost;
		}
		
		//get the table list
		public List<String> getTableList() {
			return tableList;
		}
		
		//get the expression list
		public List<Expression> getExpressionList(){
			return expressionList;
		}
		
		//get the cost
		public double getScore() {
			return cost;
		}
	}
	
}


