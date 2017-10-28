package visitor;

import java.io.IOException;
import java.util.List;
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
import physicalOperator.ExternalSortOperator;
import physicalOperator.JoinOperator;
import physicalOperator.Operator;
import physicalOperator.ProjectOperator;
import physicalOperator.SMJOperator;
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
//		PlainSelect selectBody = logJoin.getinnerOperator().;
		Expression joinCondition = logJoin.getJoinCondition();
		LogicalOperator logLeftChild = logJoin.getoutterOperator();
		LogicalOperator logRightChild = logJoin.getinnerOperator();
		logRightChild.accept(this);
		logLeftChild.accept(this);
		
		Operator leftChild = stackOp.pop();
		Operator rightChild = stackOp.pop();
		System.out.println("jMode: " + jMode);
		//System.out.println("join condition: " + joinCondition.toString());
		if (jMode==0){
			System.out.println("Using TNLJ");
			JoinOperator join = new JoinOperator(leftChild, rightChild,joinCondition);
			stackOp.push(join);
		}
		else if (jMode==1){
			System.out.println("Using BNLJ");
			System.out.println("BNLJ buffer size: " + jPara);
			BNLJOperator join = new BNLJOperator(leftChild, rightChild,joinCondition,jPara);
			stackOp.push(join);
		}
		else if (jMode==2){
			System.out.println("Using SMJ");
			System.out.println("SMJ buffer size: " + sPara);
			System.out.println("Sort Mode: " + sMode);
			if (sMode==0) {
				JoinAttributeVisitor visitor = new JoinAttributeVisitor();
	        	joinCondition.accept(visitor);
				SortOperator leftSort = new SortOperator(leftChild, visitor.getLeftAttr());
				SortOperator rightSort = new SortOperator(rightChild, visitor.getRightAttr());
				SMJOperator join = new SMJOperator(leftSort, rightSort, joinCondition);
				stackOp.push(join);
			}
			else if (sMode==1) {
	        	JoinAttributeVisitor visitor = new JoinAttributeVisitor();
	        	joinCondition.accept(visitor);
				ExternalSortOperator leftSorted = new ExternalSortOperator(leftChild, visitor.getLeftAttr(), sPara);
				ExternalSortOperator rightSorted = new ExternalSortOperator(rightChild,visitor.getRightAttr(),sPara);
				SMJOperator join = new SMJOperator(leftSorted, rightSorted, joinCondition);
				stackOp.push(join);
			}
		}
	}



//	private ExternalSortOperator ExternalSortOperator(Operator leftChild,
//			int sPara2, Expression joinCondition) {
//		// TODO Auto-generated method stub
//		return null;
//	}


	@Override
	public void visit(LogicalProjectOperator logProject) throws IOException {
		//System.out.println("haha");
		PlainSelect selectBody = logProject.getPlainSelect();
		LogicalOperator logChild = logProject.getchildOperator();
		logChild.accept(this);
		
		Operator child = stackOp.pop();
		ProjectOperator project = new ProjectOperator(selectBody, child);
		stackOp.push(project);
	}



	@Override
	public void visit(LogicalScanOperator logDistinct) throws IOException {
		String table = logDistinct.getTableName();
		//ScanOperator scan = new ScanOperator(table);
		//ScanOperatorHuman scan = new ScanOperatorHuman(table);
		ScanOperatorBinary scan = new ScanOperatorBinary(table);
		stackOp.push(scan);
	}



	@Override
	public void visit(LogicalSelectOperator logSelect) throws IOException {
		Expression selectCondition = logSelect.getSelectCondition();
		LogicalOperator logChild = logSelect.getchildOperator();
		logChild.accept(this);
		
		Operator child = stackOp.pop();
		SelectOperator select = new SelectOperator(selectCondition, child);
		stackOp.push(select);
	}



	@Override
	public void visit(LogicalSortOperator logSort) throws IOException {
		PlainSelect selectBody = logSort.getPlainSelect();
		LogicalOperator logChild = logSort.getchildOperator();
		logChild.accept(this);
		List selectItem = selectBody.getOrderByElements();
		
		Operator child = stackOp.pop();
		System.out.println("Sort Mode: " + sMode);
		if (sMode==0) {
			SortOperator sort = new SortOperator(child, selectBody);
			stackOp.push(sort);
		}
		else if (sMode==1){
			ExternalSortOperator sort = new ExternalSortOperator(child, selectItem, sPara);
			stackOp.push(sort);
		}
	}
	
	
	public void interpretConfig(){
		Catalog catalog = Catalog.getInstance();
		String jConfig = catalog.getJoinConfig();
		String sConfig = catalog.getSortConfig();
		jMode = Character.getNumericValue((jConfig.charAt(0)));
		sMode = Character.getNumericValue((sConfig.charAt(0)));
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
