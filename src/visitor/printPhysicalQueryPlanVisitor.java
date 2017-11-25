package visitor;

import physicalOperator.BNLJOperator;
import physicalOperator.DuplicateEliminationOperators;
import physicalOperator.ExternalSortOperator;
import physicalOperator.IndexScanOperator;
import physicalOperator.JoinOperator;
import physicalOperator.ProjectOperator;
import physicalOperator.SMJOperator;
import physicalOperator.ScanOperator;
import physicalOperator.ScanOperatorBinary;
import physicalOperator.ScanOperatorHuman;
import physicalOperator.SelectOperator;
import physicalOperator.SortOperator;

/** Class used to visit each physical operator then
 *  provide a result of a string represent Physical operator plan */
public class printPhysicalQueryPlanVisitor {

	
	private String result;
	private int numOfDash;
	
	public printPhysicalQueryPlanVisitor() {
		result = "";
		numOfDash = 0;
	}
	
	private String prefix(int n) {
		String pre = "";
		for(int i=0; i<n; i++){
            pre = pre + "-";
		}
		return pre;
	}
	
	/** @return Get string result*/
	public String getResult(){
		int l = result.length();
		return result.substring(0, l-1); // Strip final \n
	}
	
	
	public void visit(IndexScanOperator operator) {
		result += prefix(numOfDash) + "IndexScan" + "[" 
				+ operator.getOTName() + "]" + '\n';
	}
	
	public void visit(ScanOperator operator) {
		result += prefix(numOfDash) + "TableScan" + "[" 
				+ operator.getOTName() + "]" + '\n';
	}
	
	public void visit(ScanOperatorBinary operator) {
		result += prefix(numOfDash) + "TableScan" + "[" 
				+ operator.getOTName() + "]" + '\n';
	}

	public void visit(ScanOperatorHuman operator) {
		result += prefix(numOfDash) + "TableScan" + "[" 
				+ operator.getOTName() + "]" + '\n';
	}
	
	
	
	
	public void visit(JoinOperator operator) {
		result += prefix(numOfDash) + "TNLJ"  + "[" 
				+ operator.getConditionString() + "]" + '\n';
		numOfDash += 1;
		operator.getOutterChild().accept(this);
		operator.getInnerChild().accept(this);
		numOfDash -= 1;
	}
	
	public void visit(BNLJOperator operator) {
		result += prefix(numOfDash) + "BNLJ"  + "[" 
				+ operator.getConditionString() + "]" + '\n';
		numOfDash += 1;
		operator.getOutterChild().accept(this);
		operator.getInnerChild().accept(this);
		numOfDash -= 1;
	}
	
	public void visit(SMJOperator operator) {
		result += prefix(numOfDash) + "SMJ" + "[" 
				+ operator.getConditionString() + "]" + '\n';
		numOfDash += 1;
		operator.getOutterChild().accept(this);
		operator.getInnerChild().accept(this);
		numOfDash -= 1;
	}

	
	
	public void visit(
			DuplicateEliminationOperators operator) {
		result += prefix(numOfDash) + "DupElim" + '\n';
		numOfDash += 1;
		operator.getChild().accept(this);
		numOfDash -= 1;
	}

	

	public void visit(ExternalSortOperator operator) {
		result += prefix(numOfDash) + "ExternalSort" + 
				operator.getSortField() + '\n';
		numOfDash += 1;
		operator.getChild().accept(this);
		numOfDash -= 1;
	}

	public void visit(ProjectOperator operator) {
		result += prefix(numOfDash) + "Project"
				+ operator.getProjectField() + '\n';
		numOfDash += 1;
		operator.getChild().accept(this);
		numOfDash -= 1;
	}
	
	public void visit(SelectOperator operator) {
		result += prefix(numOfDash) + "Select" + "[" 
				+ operator.getConditionString() + "]" + '\n';
		numOfDash += 1;
		operator.getChild().accept(this);
		numOfDash -= 1;
	}

	public void visit(SortOperator operator) {
		result += prefix(numOfDash) + "InternalSort" 
				+ operator.getSortField()  + '\n';
		numOfDash += 1;
		operator.getChild().accept(this);
		numOfDash -= 1;
	}

}
