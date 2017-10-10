package visitor;

import java.io.IOException;
import java.util.Stack;

import logicalOperator.LogicalDuplicateEliminationOperators;
import logicalOperator.LogicalJoinOperator;
import logicalOperator.LogicalOperator;
import logicalOperator.LogicalProjectOperator;
import logicalOperator.LogicalScanOperator;
import logicalOperator.LogicalSelectOperator;
import logicalOperator.LogicalSortOperator;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.PlainSelect;
import physicalOperator.DuplicateEliminationOperators;
import physicalOperator.JoinOperator;
import physicalOperator.Operator;
import physicalOperator.ProjectOperator;
import physicalOperator.ScanOperator;
import physicalOperator.SelectOperator;
import physicalOperator.SortOperator;

public class PhysicalPlanBuilder implements PlanVisitor {
	
	private Stack<Operator> stackOp;
	
	public PhysicalPlanBuilder() {
		stackOp = new Stack<Operator>();
	}
	
	
	/** @return the root of the entire query plan */
	public Operator getRoot() {
		return stackOp.pop();
	}



	@Override
	public void visit(LogicalDuplicateEliminationOperators logDistinct) throws IOException {
		// TODO Auto-generated method stub
		LogicalOperator logChild = logDistinct.getchildOperator();
		logChild.accept(this);
		
		Operator child = stackOp.pop();
		DuplicateEliminationOperators distinct = new DuplicateEliminationOperators(child);
		stackOp.push(distinct);
	}



	@Override
	public void visit(LogicalJoinOperator logJoin) throws IOException {
		// TODO Auto-generated method stub
		Expression joinCondition = logJoin.getJoinCondition();
		LogicalOperator logLeftChild = logJoin.getoutterOperator();
		LogicalOperator logRightChild = logJoin.getinnerOperator();
		logRightChild.accept(this);
		logLeftChild.accept(this);
		
		Operator leftChild = stackOp.pop();
		Operator rightChild = stackOp.pop();
		JoinOperator join = new JoinOperator(leftChild, rightChild,joinCondition);
		stackOp.push(join);
	}



	@Override
	public void visit(LogicalProjectOperator logProject) throws IOException {
		// TODO Auto-generated method stub
		PlainSelect selectBody = logProject.getPlainSelect();
		LogicalOperator logChild = logProject.getchildOperator();
		logChild.accept(this);
		
		Operator child = stackOp.pop();
		ProjectOperator project = new ProjectOperator(selectBody, child);
		stackOp.push(project);
	}



	@Override
	public void visit(LogicalScanOperator logDistinct) throws IOException {
		// TODO Auto-generated method stub
		String table = logDistinct.getTableName();
		ScanOperator scan = new ScanOperator(table);
		stackOp.push(scan);
	}



	@Override
	public void visit(LogicalSelectOperator logSelect) throws IOException {
		// TODO Auto-generated method stub
		Expression selectCondition = logSelect.getSelectCondition();
		LogicalOperator logChild = logSelect.getchildOperator();
		logChild.accept(this);
		
		Operator child = stackOp.pop();
		SelectOperator select = new SelectOperator(selectCondition, child);
		stackOp.push(select);
	}



	@Override
	public void visit(LogicalSortOperator logSort) throws IOException {
		// TODO Auto-generated method stub
		PlainSelect selectBody = logSort.getPlainSelect();
		LogicalOperator logChild = logSort.getchildOperator();
		logChild.accept(this);
		
		Operator child = stackOp.pop();
		SortOperator sort = new SortOperator(child, selectBody);
		stackOp.push(sort);
	}
	
}
