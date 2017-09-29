package ExpressionVisitor;
import ExpressionTree.ANDexpression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.schema.Column;
/**
 * Tree visitor interface
 * 
 */
public interface ExpressionVisior {
	//for long value
	void visit(LongValue Expression);
	//for value like A.id
	void visit(Column Expression);
	//for "AND"
	void visit(AndExpression Expression);
	
	void visit(EqualsTo Expression);
	
	void visit(NotEqualsTo Expression);
	
	void visit(GreaterThan Expression);
	
	void visit(GreaterThanEquals Expression);
	
	void visit(MinorThan Expression);
	
	void visit(MinorThanEquals Expression);
}
