package visitor;

import java.io.IOException;

import logicalOperator.LogicalDuplicateEliminationOperators;
import logicalOperator.LogicalJoinOperator;
import logicalOperator.LogicalProjectOperator;
import logicalOperator.LogicalScanOperator;
import logicalOperator.LogicalSelectOperator;
import logicalOperator.LogicalSortOperator;


/** Class used to visit each physical operator then
 *  provide a result of a string represent Logical operator plan */
public class printLogicalQueryPlanVisitor {
	
	private String result;
	private int numOfDash;
	
	public printLogicalQueryPlanVisitor() {
		result = "";
		numOfDash = 0;
	}
	
	/** @return Get string result*/
	public String getResult(){
		int l = result.length();
		return result.substring(0, l-1); // Strip final \n
	}
	
	private String prefix(int n) {
		String pre = "";
		for(int i=0; i<n; i++){
            pre = pre + "-";
		}
		return pre;
	}
	
	public void visit(LogicalScanOperator loperator) {
		result += prefix(numOfDash) + "Leaf" + "[" 
				+ loperator.getTableName() + "]" + '\n';
	}
	
	
	public void visit(LogicalDuplicateEliminationOperators loperator) throws IOException {
		result += prefix(numOfDash) + "DupElim" + '\n';
		numOfDash += 1;
		loperator.getchildOperator().accept(this);
		numOfDash -= 1;
	}

	public void visit(LogicalProjectOperator loperator) throws IOException {
		result += prefix(numOfDash) + "Project"
				+ loperator.getProjectField() + '\n';
		numOfDash += 1;
		loperator.getchildOperator().accept(this);
		numOfDash -= 1;
	}


	public void visit(LogicalSelectOperator loperator) throws IOException {
		result += prefix(numOfDash) + "Select" + "[" 
				+ loperator.getConditionString() + "]" + '\n';
		numOfDash += 1;
		loperator.getchildOperator().accept(this);
		numOfDash -= 1;
	}
	
	public void visit(LogicalSortOperator loperator) throws IOException {
		result += prefix(numOfDash) + "Sort" + 
				loperator.getSortField() + '\n';
		numOfDash += 1;
		loperator.getchildOperator().accept(this);
		numOfDash -= 1;
	}
	
	
	
	public void visit(LogicalJoinOperator loperator) throws IOException {
		result += prefix(numOfDash) + "Join"  + "[" 
				+ loperator.getConditionString() + "]" + '\n';
		numOfDash += 1;
		loperator.getOutterChild().accept(this);
		loperator.getInnerChild().accept(this);
		numOfDash -= 1;
	}
	
	

}
