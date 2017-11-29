package visitor;

import java.io.IOException;

import logicalOperator.LogicalDuplicateEliminationOperators;
import logicalOperator.LogicalJoinOperator;
import logicalOperator.LogicalProjectOperator;
import logicalOperator.LogicalScanOperator;
import logicalOperator.LogicalSelectOperator;
import logicalOperator.LogicalSortOperator;
import logicalOperator.LogicalUnionJoinOperator;

public interface PlanVisitor {
	
	void visit(LogicalDuplicateEliminationOperators logDistinct) throws IOException;
	
	void visit(LogicalJoinOperator logJoin) throws IOException;
	
	void visit(LogicalProjectOperator logProject) throws IOException;
	
	void visit(LogicalScanOperator logDistinct) throws IOException;
	
	void visit(LogicalSelectOperator logSelect) throws IOException;
	
	void visit(LogicalSortOperator logSort) throws IOException;

	void visit(LogicalUnionJoinOperator logUnionJoin) throws IOException;
	
}
