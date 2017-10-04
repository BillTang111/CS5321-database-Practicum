package visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import Database_Catalog.Catalog;
import Tuple.Tuple;
import net.sf.jsqlparser.expression.AllComparisonExpression;
import net.sf.jsqlparser.expression.AnyComparisonExpression;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.InverseExpression;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.TimeValue;
import net.sf.jsqlparser.expression.TimestampValue;
import net.sf.jsqlparser.expression.WhenClause;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseAnd;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseOr;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseXor;
import net.sf.jsqlparser.expression.operators.arithmetic.Concat;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.expression.operators.relational.Matches;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SubSelect;

/**
 * This class is used to visitor expression.
 * 
 * @author Lini Tan, lt398
 */
public class visitor implements ExpressionVisitor {
	HashMap schema;
	Boolean result;
	ArrayList field;
	private Stack<Long> TreeStack;
	ArrayList tuple;
	HashMap tupleMap;
	HashMap<String, String> pairAlias;
	
	public visitor(Tuple a) {
		result = true;
		schema = Catalog.getInstance().getSchema();
		TreeStack = new Stack<Long>();
		tuple = a.getTuple();
		tupleMap = a.getTupleMap();
		
		Catalog data = Catalog.getInstance();
		pairAlias = data.getPairAlias();
	}
	
	public Boolean getResult(){
		return result;
	}
	
	@Override
	public void visit(NullValue arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void visit(Function arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(InverseExpression arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(JdbcParameter arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(DoubleValue arg0) {
		// TODO Auto-generated method stub
		
	}

	/**This is the method when visit longValue
	 * @param longValue that to be visited
	 * */
	@Override
	public void visit(LongValue arg0) {
		// TODO Auto-generated method stub
		TreeStack.push(arg0.getValue());
		
	}

	@Override
	public void visit(DateValue arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(TimeValue arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(TimestampValue arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Parenthesis arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(StringValue arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Addition arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Division arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Multiplication arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Subtraction arg0) {
		// TODO Auto-generated method stub
		
	}

	/**This is the method when visit andExpression
	 * @param andExpression that to be visited
	 * */
	@Override
	public void visit(AndExpression arg0) {
		// TODO Auto-generated method stub
		arg0.getLeftExpression().accept(this);
		arg0.getRightExpression().accept(this);
	}

	@Override
	public void visit(OrExpression arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Between arg0) {
		// TODO Auto-generated method stub
		
	}

	/**This is the method when visit EqualsTo Expression
	 * @param EqualsTo Expression that to be visited
	 * */
	@Override
	public void visit(EqualsTo arg0) {
		// TODO Auto-generated method stub
		arg0.getRightExpression().accept(this);
		arg0.getLeftExpression().accept(this);
		result = result && (TreeStack.pop()==TreeStack.pop());	
	}

	/**This is the method when visit GreaterThan Expression
	 * @param GreaterThan Expression that to be visited
	 * */
	@Override
	public void visit(GreaterThan arg0) {
		// TODO Auto-generated method stub
		arg0.getRightExpression().accept(this);
		arg0.getLeftExpression().accept(this);
		result = result && (TreeStack.pop()>TreeStack.pop());	
	}

	/**This is the method when visit GreaterThanEquals Expression
	 * @param GreaterThanEquals Expression that to be visited
	 * */
	@Override
	public void visit(GreaterThanEquals arg0) {
		// TODO Auto-generated method stub
		arg0.getRightExpression().accept(this);
		arg0.getLeftExpression().accept(this);
		result = result && (TreeStack.pop()>=TreeStack.pop());	
		
	}

	@Override
	public void visit(InExpression arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(IsNullExpression arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(LikeExpression arg0) {
		// TODO Auto-generated method stub
		
	}

	/**This is the method when visit MinorThan Expression
	 * @param MinorThan Expression that to be visited
	 * */
	@Override
	public void visit(MinorThan arg0) {
		// TODO Auto-generated method stub
		arg0.getRightExpression().accept(this);
		arg0.getLeftExpression().accept(this);
		result = result && (TreeStack.pop()<TreeStack.pop());	
		
	}
	
	/**This is the method when visit MinorThanEquals Expression
	 * @param MinorThanEquals Expression that to be visited
	 * */
	@Override
	public void visit(MinorThanEquals arg0) {
		// TODO Auto-generated method stub
		arg0.getRightExpression().accept(this);
		arg0.getLeftExpression().accept(this);
		result = result && (TreeStack.pop()<=TreeStack.pop());	
	}

	/**This is the method when visit NotEqualsTo Expression
	 * @param NotEqualsTo Expression that to be visited
	 * */
	@Override
	public void visit(NotEqualsTo arg0) {
		// TODO Auto-generated method stub
		arg0.getRightExpression().accept(this);
		arg0.getLeftExpression().accept(this);
		result = result && (TreeStack.pop()!=TreeStack.pop());	
	}

	/**This is the method when visit column Expression
	 * @param column Expression that to be visited
	 * */
	@Override
	public void visit(Column arg0) {
		// TODO Auto-generated method stub
		// make S.A to Sailor.A
		String orginColumnField = arg0.toString();
		int dotIndex = orginColumnField.indexOf(".");
		String newColumnField = pairAlias.get(orginColumnField.substring(0, dotIndex))
				+ "." + orginColumnField.substring(dotIndex+1 , orginColumnField.length());
		
		
		//System.out.println(tupleMap.toString());
		//System.out.println(newColumnField);
		
		int index = (int) tupleMap.get(newColumnField);
		String s = (String) tuple.get(index);
		Integer i = Integer.parseInt(s);
		TreeStack.push(i.longValue());
		
		
	}

	@Override
	public void visit(SubSelect arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(CaseExpression arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(WhenClause arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ExistsExpression arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(AllComparisonExpression arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(AnyComparisonExpression arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Concat arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Matches arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(BitwiseAnd arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(BitwiseOr arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(BitwiseXor arg0) {
		// TODO Auto-generated method stub
		
	}

}
