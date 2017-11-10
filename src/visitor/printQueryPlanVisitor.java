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

public class printQueryPlanVisitor {
	
	private String result;
	
	public printQueryPlanVisitor() {
		result = "";
	}
	
	public String getResult(){
		return result;
	}
	
	
	public void visit(IndexScanOperator operator) {
		result += "IndexScanOperator";
	}
	
	public void visit(ScanOperator operator) {
		result += "ScanOperator";
	}
	
	public void visit(ScanOperatorBinary operator) {
		result += "ScanOperatorBinary";
	}

	public void visit(ScanOperatorHuman operator) {
		result += "ScanOperatorHuman";
	}
	
	
	
	
	public void visit(JoinOperator operator) {
		result += "JoinOperator--(";
		operator.getOutterChild().accept(this);
		result += " ~ ";
		operator.getInnerChild().accept(this);
		result += ")";
	}
	
	public void visit(BNLJOperator operator) {
		result += "BNLJOperator--(";
		operator.getOutterChild().accept(this);
		result += " ~ ";
		operator.getInnerChild().accept(this);
		result += ")";
	}
	
	public void visit(SMJOperator operator) {
		result += "SMJOperator--(";
		operator.getOutterChild().accept(this);
		result += " ~ ";
		operator.getInnerChild().accept(this);
		result += ")";
	}

	
	
	public void visit(
			DuplicateEliminationOperators operator) {
		result += "DuplicateEliminationOperators--(";
		operator.getChild().accept(this);
		result += ")";
	}

	public void visit(ExternalSortOperator operator) {
		result += "externalSortOperator--(";
		operator.getChild().accept(this);
		result += ")";
	}

	public void visit(ProjectOperator operator) {
		result += "ProjectOperator--(";
		operator.getChild().accept(this);
		result += ")";
	}
	
	public void visit(SelectOperator operator) {
		result += "SelectOperator--(";
		operator.getChild().accept(this);
		result += ")";
	}

	public void visit(SortOperator operator) {
		result += "SortOperator--(";
		operator.getChild().accept(this);
		result += ")";
	}


	






	

	

}
