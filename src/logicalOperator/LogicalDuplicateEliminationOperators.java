package logicalOperator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import Database_Catalog.Catalog;
import Tuple.Tuple;
import net.sf.jsqlparser.statement.select.PlainSelect;
import physicalOperator.Operator;
import visitor.PhysicalPlanBuilder;
import visitor.printLogicalQueryPlanVisitor;

/**
 * This class is used when sql query contains "distinct". It is used to remove duplicate 
 * tuple. 
 * 
 * @author Lini Tan, lt398
 */
public class LogicalDuplicateEliminationOperators extends LogicalOperator {
	
	LogicalOperator childOp;

	
	public LogicalDuplicateEliminationOperators(LogicalOperator root){
		childOp = root;
	}

	@Override
	public void accept(PhysicalPlanBuilder physicalPlanBuilder) throws IOException {
		physicalPlanBuilder.visit(this);
	}

	public LogicalOperator getchildOperator() {
		return childOp;
	}

	@Override
	public void accept(printLogicalQueryPlanVisitor lpv) throws IOException {
		lpv.visit(this);
	}
	
	

}
