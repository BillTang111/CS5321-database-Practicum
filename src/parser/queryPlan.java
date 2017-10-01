package parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import Operator.Operator;
import Operator.ProjectOperator;
import Operator.ScanOperator;
import Operator.SelectOperator;
import Tuple.Tuple;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.expression.Expression;

public class queryPlan {
	// parse the SQL into a tree structured query plan
	private Operator root;
	
	
	public queryPlan(PlainSelect selectBody) throws IOException {
		// TODO Auto-generated constructor stub
		
		// TODO wait Lini Tan implementation to get these two
		ArrayList<String> joinSequence;
		HashMap<String, ArrayList<Expression>> selectExpression;
		
		
		int tableNum = joinSequence.size();
		ArrayList<Operator> nodeBeforeJoin = null; //contain subtree of each table after scan and select
		
		for(int i = 0; i < tableNum; i++) {
			String tableName = joinSequence.get(i);
			ScanOperator scan =  new ScanOperator(tableName);
			root = scan; //First process scan all the time
			
			//If there are related select expressions of a table, process each of them 
			//to grow the subtree
			if(selectExpression.containsKey(tableName)){
				ArrayList<Expression> expressionList = selectExpression.get(tableName);
				for(Expression eachExpression: expressionList){
					SelectOperator select = new SelectOperator(eachExpression, root);
					root = select;
				}
			}
			nodeBeforeJoin.add(root); //finish subtree of this table after scan and select
		}
		
		
		// Join all the scanned and selected table together in sequence
		if (tableNum >= 2){
			JoinOperator join = new JoinOperator(nodeBeforeJoin.get(0),
					nodeBeforeJoin.get(1), joinCondition);
			root = join;
			int n = 2; // number of table already joined
			while ((tableNum - n) > 0){
				JoinOperator join = new JoinOperator(root,
						nodeBeforeJoin.get(n), joinCondition);
				root = join;
				n++;
			}
		}
		
		if (selectBody.getSelectItems().get(0)!="*") {
			ProjectOperator project = new ProjectOperator(selectBody, root);
			root = project;
		}
		
		
//		ScanOperator scan =  new ScanOperator(selectBody);
//		root = scan;
//		if (selectBody.getWhere()!=null) {
//			SelectOperator select = new SelectOperator(selectBody, root);
//			root = select;
//		}
//		if (selectBody.getSelectItems().get(0)!="*") {
//			ProjectOperator project = new ProjectOperator(selectBody, root);
//			root = project;
//		}
	}
	
//		public queryPlan(PlainSelect selectBody) throws IOException {
//			// TODO Auto-generated constructor stub
//			
//			ScanOperator scan =  new ScanOperator(selectBody);
//			root = scan;
//			if (selectBody.getWhere()!=null) {
//				SelectOperator select = new SelectOperator(selectBody, root);
//				root = select;
//			}
//			if (selectBody.getSelectItems().get(0)!="*") {
//				ProjectOperator project = new ProjectOperator(selectBody, root);
//				root = project;
//			}
//		}	
		
		
		
	public Operator getRoot() {
		return root;
	}

}
