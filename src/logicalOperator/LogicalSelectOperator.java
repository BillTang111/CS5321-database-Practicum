package logicalOperator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import Database_Catalog.Catalog;
import Tuple.Tuple;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.PlainSelect;
import visitor.PhysicalPlanBuilder;
import visitor.printLogicalQueryPlanVisitor;
import visitor.visitor;

/**
 * This class is used when sql query contains select condition.
 * 
 * @author Lini Tan, lt398
 */
public class LogicalSelectOperator extends LogicalOperator {
	
//	PlainSelect select; //Store the plainSelect object parsed from query
	Expression e;
//	BufferedReader br;
//	FromItem input;
//	String location;
	LogicalOperator childOp;
	
	public LogicalSelectOperator(Expression singleSelect, LogicalOperator op) throws IOException {
		// TODO Auto-generated constructor stub
//		input = selectBody.getFromItem();
//		location = Catalog.getInstance().getInputLocation();	
//		br = new BufferedReader(new FileReader(location + "/db/data/"+input));  
//		select = selectBody;
//		e = selectBody.getWhere();
		e = singleSelect;
		System.out.println("debug: "+e);
		childOp = op;
	}

	public Expression getSelectCondition(){
		return this.e;
	}
	
	
	public LogicalOperator getchildOperator(){
		return this.childOp;
	}

	@Override
	public void accept(PhysicalPlanBuilder physicalPlanBuilder)
			throws IOException {
		physicalPlanBuilder.visit(this);
	}

	@Override
	public void accept(printLogicalQueryPlanVisitor lpv) throws IOException {
		lpv.visit(this);
	}

	public String getConditionString() {
		return e.toString();
	}

}
