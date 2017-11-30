package parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import Database_Catalog.Catalog;
import logicalOperator.LogicalDuplicateEliminationOperators;
import logicalOperator.LogicalOperator;
import logicalOperator.LogicalProjectOperator;
import logicalOperator.LogicalScanOperator;
import logicalOperator.LogicalSelectOperator;
import logicalOperator.LogicalSortOperator;
import logicalOperator.LogicalUnionJoinOperator;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.PlainSelect;
import visitor.joinVisitor;
import UnionFind.Element;

public class newLogicalPlanBuilder {
	private LogicalOperator root;
	private Object hashMap;
	ArrayList<Element> UF;
	List<Expression> residualSelectList;
	List<Expression> residualJoinList;
	
	/** logicalQueryPlan constructor
	 * @param PlainSelect selectBody: the result parsed from Jsql parser 
	 * @param
	 * @throws IOException 
	 * 
	 * Author: Hao Rong, hr335 */
	public newLogicalPlanBuilder(PlainSelect selectBody) throws IOException {
		// sortedTable: List of table names sorted by sequence appear in Join condtion
		// nodeBeforeJoin: to store operator after scan and select
		ArrayList<LogicalOperator> nodeBeforeJoin = new ArrayList<LogicalOperator>(); 
		ArrayList<String> sortedTable = new ArrayList<String>(); 
		HashMap<String, String> pairAlias = new HashMap<String, String>();
		Catalog data = Catalog.getInstance();
		
		// 0. collect unionFind data
		UF = data.getUnionFind().getUFlist();
		residualSelectList = data.getSelectResidual();
		residualJoinList = data.getJoinResidual();
		
		
		// 1. Set up sortedTable, pairAlias
		Expression e = selectBody.getWhere();
		if (selectBody.getWhere() != null){ // if there is WHERE
			joinVisitor jVisitor = new joinVisitor(selectBody);
			e.accept(jVisitor);
			pairAlias = jVisitor.getPairAlias();
			data = Catalog.getInstance();
			data.setPairAlias(pairAlias);		
			sortedTable = jVisitor.getJoinTableList(); // original name
			// If there is no join condition, add the only table name into sortedTable
			if (sortedTable.isEmpty()){ // only involve one table
				String onlyTable = selectBody.getFromItem().toString();
				if (selectBody.getFromItem().getAlias()!=null) { //eliminate alias part from the table name
					String onlyAlias = selectBody.getFromItem().getAlias().toString();
					int index = onlyTable.indexOf(" AS ");
					onlyTable = onlyTable.substring(0, index);
					pairAlias.put(onlyAlias, onlyTable);
				}
				sortedTable.add(onlyTable);
				pairAlias.put(onlyTable, onlyTable);
			}
		}
		else { // no where condition
			String onlyTable = selectBody.getFromItem().toString();
			if (selectBody.getFromItem().getAlias()!=null) { //eliminate alias part from the table name
				String onlyAlias = selectBody.getFromItem().getAlias().toString();
				int index = onlyTable.indexOf(" AS ");
				onlyTable = onlyTable.substring(0, index);
				pairAlias.put(onlyAlias, onlyTable);
			}	
		sortedTable.add(onlyTable);
		pairAlias.put(onlyTable, onlyTable);
		data = Catalog.getInstance();
		data.setPairAlias(pairAlias);
		}
		
		// new sortedTable
		sortedTable = new ArrayList<String>(); 
		sortedTable.add(stripAStablename(selectBody.getFromItem().toString()));
		List joinTableList = selectBody.getJoins();
		if (joinTableList!=null){
			for(Object t:joinTableList){
				sortedTable.add(stripAStablename(t.toString()));
			}
		}
		System.out.println("debug01: "+sortedTable.toString());
		int tableNum = sortedTable.size(); // number of table involved in this query
		
		
		
		
		// 2. Use UnionFind information building Scan and Select node,
		//    Join them under unionJoin node
		for(int i = 0; i < tableNum; i++) {
			String tableName = sortedTable.get(i); //original name
			LogicalScanOperator scan =  new LogicalScanOperator(tableName);
			root = scan; //First process scan all the time
			
			Expression oneTableSelectExpr = null; // All select expression related to one table
			
			for(Element eBox: UF) {
				// Within one Table equal select without other bounds: R.A=R.B / R.A
				if (eBox.getEquality()==null && eBox.getLowerBound()==null && eBox.getUpperBound()==null) {
					Column firstAttr = null;
					for (Column attr: eBox.getattri()) {
						if (deAlias(attr.getTable().getName()).equals(tableName)) {
							if (firstAttr==null) {
								firstAttr = attr;
							}
							else {
								oneTableSelectExpr = addExpression(oneTableSelectExpr, new EqualsTo(firstAttr, attr));
							}
						}
					}
				}
				else { // Within one Table that Have some bounds: R.A=20 and R.B=20
					for (Column attr: eBox.getattri()) {
						if(deAlias(attr.getTable().getName()).equals(tableName)) {
							if(eBox.getEquality()!=null) { //R.A=20 
								EqualsTo eqExpr = new EqualsTo(attr,new LongValue(eBox.getEquality()));
								oneTableSelectExpr = addExpression(oneTableSelectExpr, eqExpr);
							} else {
								if(eBox.getLowerBound()!=null) { //R.A>=20
									GreaterThanEquals greatEqExpr = new GreaterThanEquals(attr,new LongValue(eBox.getLowerBound()));
									oneTableSelectExpr = addExpression(oneTableSelectExpr, greatEqExpr);
								}
								if(eBox.getUpperBound()!=null) { //R.A<=20
									//System.out.println("debug: "+eBox);
									MinorThanEquals minorEqExpr = new MinorThanEquals(attr,new LongValue(eBox.getUpperBound()));
									oneTableSelectExpr = addExpression(oneTableSelectExpr, minorEqExpr);
								}
							}
						}
					}
				}
			}
			for(Expression reSelectEx:residualSelectList){ //R.A<>20 or R.A>=R.B
				if(deAlias(reSelectEx.toString()).equals(tableName)) {
					//System.out.println("debugg: "+reSelectEx);
					oneTableSelectExpr = addExpression(oneTableSelectExpr, reSelectEx);
				}
			}
			
			if (oneTableSelectExpr!=null){
				LogicalSelectOperator select = new LogicalSelectOperator(oneTableSelectExpr, root);
				root = select;
			}
			
			nodeBeforeJoin.add(root);
		}
		
		if(nodeBeforeJoin.size()==1){
			root = nodeBeforeJoin.get(0);
			System.out.println("nodeBeforeJoin: only 1 table, skip join");
		}
		else{
			LogicalUnionJoinOperator logicalUJ = new LogicalUnionJoinOperator(nodeBeforeJoin, sortedTable);
			root = logicalUJ;
		}
					

		// 4. Add projection node to current tree if there is project condition
		System.out.println("Project? " + (selectBody.getSelectItems().get(0).equals("*")));
		if (selectBody.getSelectItems()!=null) {
			//System.out.println("add project!");
			LogicalProjectOperator project = new LogicalProjectOperator(selectBody, root);
			root = project;
		}
				
		// 5. Add OrderBy node to current tree if there is project condition
		if (selectBody.getOrderByElements()!=null) {
			LogicalSortOperator Sort = new LogicalSortOperator(root, selectBody);
			root = Sort;
		}
				
				
		// 6. Add DuplicateElimination node to current tree if there is project condition
		if (selectBody.getDistinct()!=null) {
			if (selectBody.getOrderByElements()==null){
				LogicalSortOperator Sort = new LogicalSortOperator(root, selectBody);
				root = Sort;
			}
			LogicalDuplicateEliminationOperators distinct = new LogicalDuplicateEliminationOperators(root);
			root = distinct;
		}
	}
			
			
	private String stripAStablename(String string) {
		int asIndex = string.indexOf(" AS ");
		if(asIndex==-1){
			return string;
		}
		return string.substring(0, asIndex);
	}


	/** Join Expression together */	
	private Expression addExpression(Expression oldExpression, Expression newExpression) {
		if(oldExpression == null) return newExpression;
		return new AndExpression(oldExpression, newExpression);
	}		
				
	
	/** Find the string before the first dot(.) as input String
	 *  Replace the Alias (If exist) to the original table name in the input String 
	 *  Input: R.A or R 
	 *  @return Reserve */
	private String deAlias(String attr) {
		Catalog data = Catalog.getInstance();
		HashMap<String, String> pairAlias = data.getPairAlias();
		int dotIndex = attr.indexOf(".");
		if (dotIndex == -1){
			return pairAlias.get(attr);
		}
		
		String aliasTableName = attr.substring(0, dotIndex);
		return pairAlias.get(aliasTableName);
	}

	
	/** @return the root of the entire query plan */
	public LogicalOperator getRoot() {
		return root;
	}
				
}