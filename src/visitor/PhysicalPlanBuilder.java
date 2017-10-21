package visitor;

import java.io.IOException;
import java.util.Stack;

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
import physicalOperator.BNLJOperator;
import physicalOperator.DuplicateEliminationOperators;
import physicalOperator.JoinOperator;
import physicalOperator.Operator;
import physicalOperator.ProjectOperator;
import physicalOperator.ScanOperator;
import physicalOperator.ScanOperatorBinary;
import physicalOperator.ScanOperatorHuman;
import physicalOperator.SelectOperator;
import physicalOperator.SortOperator;

public class PhysicalPlanBuilder implements PlanVisitor {
	
	private Stack<Operator> stackOp;
	int jMode; //0 for TNLJ, 1 for BNLJ, or 2 for SMJ
	int jPara; //BNLJ size
	int sMode; //0 for in-memory sort, 1 for external sort
	int sPara; //number of buffer pages for external sort
	
	public PhysicalPlanBuilder() {
		stackOp = new Stack<Operator>();
		interpretConfig();
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
		if (jMode==0){
			JoinOperator join = new JoinOperator(leftChild, rightChild,joinCondition);
			stackOp.push(join);
		}
		else if (jMode==1){
			BNLJOperator join = new BNLJOperator(leftChild, rightChild,joinCondition,jPara);
			stackOp.push(join);
		}
		else if (jMode==2){
			
		}
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
		//ScanOperator scan = new ScanOperator(table);
		//ScanOperatorHuman scan = new ScanOperatorHuman(table);
		ScanOperatorBinary scan = new ScanOperatorBinary(table);
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
	
	
	public void interpretConfig(){
		Catalog catalog = Catalog.getInstance();
		String jConfig = catalog.getJoinConfig();
		String sConfig = catalog.getSortConfig();
		jMode = (int)(jConfig.charAt(0));
		sMode = (int)(sConfig.charAt(0));
		if (jMode==1){
			String BNLJsize = jConfig.split(" ")[1];
			jPara = Integer.parseInt(BNLJsize);
		}
		if (sMode==1){
			String ESortSize = sConfig.split(" ")[1];
			sPara = Integer.parseInt(ESortSize);
		}
	}
	
}
