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
	
	public printLogicalQueryPlanVisitor() {
		result = "";
	}
	
	/** @return Get string result*/
	public String getResult(){
		return result;
	}
	
	public void visit(LogicalScanOperator loperator) {
		result += "LogicalScanOperator";
	}
	
	
	
	
	public void visit(LogicalDuplicateEliminationOperators loperator) throws IOException {
		result += "LogicalDuplicateEliminationOperators--(";
		loperator.getchildOperator().accept(this);
		result += ")";
	}

	public void visit(LogicalProjectOperator loperator) throws IOException {
		result += "LogicalProjectOperator--(";
		loperator.getchildOperator().accept(this);
		result += ")";
	}


	public void visit(LogicalSelectOperator loperator) throws IOException {
		result += "LogicalSelectOperator--(";
		loperator.getchildOperator().accept(this);
		result += ")";
	}
	
	public void visit(LogicalSortOperator loperator) throws IOException {
		result += "LogicalSortOperator--(";
		loperator.getchildOperator().accept(this);
		result += ")";
	}
	
	
	
	public void visit(LogicalJoinOperator loperator) throws IOException {
		result += "LogicalJoinOperator--(";
		loperator.getOutterChild().accept(this);
		result += " ~ ";
		loperator.getInnerChild().accept(this);
		result += ")";
	}
	
	

}
