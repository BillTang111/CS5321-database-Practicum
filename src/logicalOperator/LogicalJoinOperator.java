package logicalOperator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Database_Catalog.Catalog;
import Tuple.Tuple;
import net.sf.jsqlparser.statement.select.PlainSelect;
import visitor.visitor;
import net.sf.jsqlparser.expression.Expression;

/**
 * This class is used when sql query contains join condition.
 * 
 * @author Lini Tan, lt398
 */
public class LogicalJoinOperator extends LogicalOperator {
	
	Expression expression;
	LogicalOperator outter;
	LogicalOperator inner;
	
	public LogicalJoinOperator(LogicalOperator left, LogicalOperator right, Expression joinExpression) {
		// TODO Auto-generated constructor stub
		expression = joinExpression;
		outter = left;
		inner = right;
	}
	
	public Expression getJoinCondition(){
		return this.expression;
	}
	
	public LogicalOperator getinnerOperator(){
		return this.inner;
	}
	
	public LogicalOperator getoutterOperator(){
		return this.outter;
	}

}
