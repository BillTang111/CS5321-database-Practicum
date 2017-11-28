package parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import Database_Catalog.Catalog;
import logicalOperator.LogicalDuplicateEliminationOperators;
import logicalOperator.LogicalJoinOperator;
import logicalOperator.LogicalOperator;
import logicalOperator.LogicalProjectOperator;
import logicalOperator.LogicalScanOperator;
import logicalOperator.LogicalSelectOperator;
import logicalOperator.LogicalSortOperator;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.PlainSelect;
import physicalOperator.SortOperator;
import visitor.joinVisitor;

public class LogicalQueryPlan {
	
	private LogicalOperator root;
	private Object hashMap;
	
	/** logicalQueryPlan constructor
	 * @param PlainSelect selectBody: the result parsed from Jsql parser 
	 * @throws IOException 
	 * 
	 * Author: Hao Rong, hr335 */
	public LogicalQueryPlan(PlainSelect selectBody) throws IOException {
		// sortedTable: List of table names sorted by sequence appear in Join condtion
		// selectConditionMap: Key: table name, Value: select conditions(>=1) related to table
		// joinConditionMap: Key: pair of table name, Value: join condition related to tables in the key
		// joinPair: pair of table name, sorted by appear sequence in join
		// nodeBeforeJoin: to store operator after scan and select
		ArrayList<String> sortedTable = new ArrayList<String>(); 
		HashMap<String, ArrayList<Expression>> selectConditionMap = new HashMap<String, ArrayList<Expression>>(); 
		HashMap<ArrayList<String>, Expression> joinConditionMap = new HashMap<ArrayList<String>, Expression>();
		ArrayList<ArrayList<String>> joinPair = new ArrayList<ArrayList<String>>(); 
		ArrayList<LogicalOperator> nodeBeforeJoin = new ArrayList<LogicalOperator>(); 
		HashMap<String, String> pairAlias = new HashMap<String, String>();
		Catalog data = Catalog.getInstance();
		
		// 1. Pre-processing the statement from Jsql parser
		Expression e = selectBody.getWhere();
		if (selectBody.getWhere() != null){ // if there is WHERE
			joinVisitor jVisitor = new joinVisitor(selectBody);
			e.accept(jVisitor);
						
			pairAlias = jVisitor.getPairAlias();
			data = Catalog.getInstance();
			data.setPairAlias(pairAlias);
						
			sortedTable = jVisitor.getJoinTableList(); // original name
			selectConditionMap = jVisitor.getSelectConditionMap(); // key:original value:might be Alias name
			System.out.println("Select condition" + selectConditionMap);
			//TODO next round
//			if (selectConditionMap!= null){
//				sortSelectCondition(selectConditionMap);
//			}
			
			
			
			joinConditionMap = jVisitor.getJoinConditionMap(); // key:original value:might be Alias name
			joinPair = jVisitor.getJoinPair(); // original name

			
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
		int tableNum = sortedTable.size(); // number of table involved in this query
			
		
		// 2. Add Scan and Select node, processed subtree stored in nodeBeforeJoin
		for(int i = 0; i < tableNum; i++) {
			String tableName = sortedTable.get(i); //original name
			LogicalScanOperator scan =  new LogicalScanOperator(tableName);
			root = scan; //First process scan all the time		
			//If there are related select expressions of a table, process each of them 
			//to grow the subtree
			
			
			
			if(selectConditionMap.containsKey(tableName)){
				ArrayList<Expression> selectConditionList = selectConditionMap.get(tableName);
				//System.out.println(selectConditionList);
				for(Expression eachExpression: selectConditionList){
					LogicalSelectOperator select = new LogicalSelectOperator(eachExpression, root);
					root = select;
				}
			}
			
			nodeBeforeJoin.add(root); //finish subtree of this table after scan and select
		}
		ArrayList<LogicalOperator> parentsOfTable = (ArrayList<LogicalOperator>) nodeBeforeJoin.clone(); // parents nodes of each table
						
		
		// 3. Join all the scanned and selected table together in sequence, set the root node
		if (tableNum >= 2){ // if there are more than 2 table, which means there are joins
			int n = 1; 
			ArrayList<String> pairKey = joinPair.get(n-1); // key
			Expression joinCondition = null;
			LogicalJoinOperator Join = null;
						
			while (n < tableNum){ // when there is still table left to join
				pairKey = joinPair.get(n-1); // get next pair of join table as key
				joinCondition = joinConditionMap.get(pairKey);
				int leftIndex = sortedTable.indexOf(pairKey.get(0));
				int rightIndex = sortedTable.indexOf(pairKey.get(1));
							
				LogicalOperator leftOriginalParent= parentsOfTable.get(leftIndex);
				LogicalOperator rightOriginalParent= parentsOfTable.get(rightIndex);
				
				// always build a left-deep tree
				if (!rightOriginalParent.equals(nodeBeforeJoin.get(rightIndex))) {
					Join = new LogicalJoinOperator(rightOriginalParent, leftOriginalParent, joinCondition);
				} 
				else {
					Join = new LogicalJoinOperator(leftOriginalParent, rightOriginalParent, joinCondition);
				}
							
				// substitute parents of all the node belong to left,right child to current highest join
				for (int i = 0; i < parentsOfTable.size(); i++) {
					if(leftOriginalParent.equals(parentsOfTable.get(i))){
						parentsOfTable.set(i, Join);
					}
				}
						
				for (int h = 0; h < parentsOfTable.size(); h++) {
					if(rightOriginalParent.equals(parentsOfTable.get(h))){
						parentsOfTable.set(h, Join);
					}
				}
							
				n++;
			}
		}			
		root = parentsOfTable.get(0); // after join (or no join), set root to the highest parents

		
		// 4. Add projection node to current tree if there is project condition
		System.out.println("Project? " + (selectBody.getSelectItems().get(0).equals("*")));
//		if (selectBody.getSelectItems().get(0).equals("*")) {
//			LogicalProjectOperator project = new LogicalProjectOperator(selectBody, root);
//			root = project;
//			//System.out.println("Project! ");
//		}
		if (selectBody.getSelectItems()!=null) {
		LogicalProjectOperator project = new LogicalProjectOperator(selectBody, root);
		root = project;
		//System.out.println("Project! ");
	}
		
		
		// 5. Add OrderBy node to current tree if there is project condition
		if (selectBody.getOrderByElements()!=null) {
			LogicalSortOperator Sort = new LogicalSortOperator(root, selectBody);
			root = Sort;
		}
		
		
		// 5. Add DuplicateElimination node to current tree if there is project condition
		if (selectBody.getDistinct()!=null) {
			if (selectBody.getOrderByElements()==null){
				LogicalSortOperator Sort = new LogicalSortOperator(root, selectBody);
				root = Sort;
			}
			LogicalDuplicateEliminationOperators distinct = new LogicalDuplicateEliminationOperators(root);
			root = distinct;
		}
	}
	
	
//	private void sortSelectCondition(
			//TODO
//			HashMap<String, ArrayList<Expression>> originSelectCondition) {
//		int size = originSelectCondition.size();
//		for (int i=0; i<size; i++){
//			ArrayList<Expression> eachTableConditon = originSelectCondition.get(i);
//			Comparator<? super Expression> c;
//			//eachTableConditon.sort(c);
//			}
//			
//			
//		}
	
		
		
	


	/** @return the root of the entire query plan */
	public LogicalOperator getRoot() {
		return root;
	}
		
}

			
	
