package visitor;

import java.util.ArrayList;
import java.util.List;

import UnionFind.Element;
import UnionFind.UnionFind;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.AllComparisonExpression;
import net.sf.jsqlparser.expression.AnyComparisonExpression;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
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
 * This class walk through the expression to build unionFind
 * 
 * Author: Lini Tan lt398
 * */
public class unionFindVisitor implements ExpressionVisitor {
	private UnionFind unionFind;
	private List<Expression> joinResidual;
	private List<Expression> selectResidual;
	
	
	public unionFindVisitor(){
		unionFind = new UnionFind();
		joinResidual = new ArrayList<Expression>();
		selectResidual = new ArrayList<Expression>();
	}

	public UnionFind getUnionfind(){
		return unionFind;
	}
	
	public List<Expression> getJoinResidual(){
		return joinResidual;
	}
	
	public List<Expression> getSelectResidual(){
		return selectResidual;
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

	@Override
	public void visit(LongValue arg0) {
		// TODO Auto-generated method stub
		
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
	
	/**
	 * if the comparison has the form att1 = att2, find the two elements
	 * containing att1 and att2 and union them
	 * if the comparison has the form att OP val, find the element containing
	 * att and update the numeric bound
	 * */
	@Override
	public void visit(EqualsTo arg0) {
		// TODO Auto-generated method stub
		Expression left = arg0.getLeftExpression();
		Expression right = arg0.getRightExpression();
		if (left instanceof Column){
			if (right instanceof Column){
				Element leftElement = unionFind.FindElement(left.toString());
				Element rightElement = unionFind.FindElement(right.toString());
				unionFind.merge(leftElement, rightElement);
			}else{
				Element leftElement = unionFind.FindElement(left.toString());
				String rightNum = right.toString();
				Long equal = Long.valueOf(rightNum);
				Long lower = leftElement.getLowerBound();
				Long upper = leftElement.getUpperBound();
				unionFind.updateElement(leftElement, lower, upper, equal);
			}
		}
		
	}

	/**
	 * if the comparison has the form att1 OP att2, put them in the residual list.
	 * if the comparison has the form att OP val, find the element containing
	 * att and update the numeric bound
	 * */
	@Override
	public void visit(GreaterThan arg0) {
		// TODO Auto-generated method stub
		Expression left = arg0.getLeftExpression();
		Expression right = arg0.getRightExpression();
		if(left instanceof Column){
			if(right instanceof Column){
				//if they are from the same table, add to select residual list.
				if((((Column) left).getTable()).equals(((Column) right).getTable())){
//					GreaterThan greaterthan = arg0;
//					greaterthan.setLeftExpression(left);
//					greaterthan.setRightExpression(right);
//					selectResidual.add(greaterthan); 
					selectResidual.add(arg0);
				}else{
					joinResidual.add(arg0);
				}
			}else{
				Element leftElement = unionFind.FindElement(left.toString());
				String rightNum = right.toString();
				Long lower = Long.valueOf(rightNum);
				Long oldLower = leftElement.getLowerBound();
				if(oldLower!=null){
					lower = Math.max(lower, oldLower);	
				}
				
				Long equal = leftElement.getEquality();
				Long upper = leftElement.getUpperBound();
				if(equal == null){
					unionFind.updateElement(leftElement, lower, upper, equal);
				}
			}
		}
	}

	/**
	 * if the comparison has the form att1 OP att2, put them in the residual list.
	 * if the comparison has the form att OP val, find the element containing
	 * att and update the numeric bound
	 * */
	@Override
	public void visit(GreaterThanEquals arg0) {
		// TODO Auto-generated method stub
		Expression left = arg0.getLeftExpression();
		Expression right = arg0.getRightExpression();
		if(left instanceof Column){
			if(right instanceof Column){
				//if they are from the same table, add to select residual list.
				if((((Column) left).getTable()).equals(((Column) right).getTable())){
//					GreaterThan greaterthanEqual = arg0;
//					greaterthanEqual.setLeftExpression(left);
//					greaterthanEqual.setRightExpression(right);
//					selectResidual.add(greaterthanEqual); 
					selectResidual.add(arg0);
				}else{
					joinResidual.add(arg0);
				}
			}else{
				Element leftElement = unionFind.FindElement(left.toString());
				String rightNum = right.toString();
				Long lower = Long.valueOf(rightNum);
				lower = lower-1;
				Long oldLower = leftElement.getLowerBound();
				if(oldLower != null){
					lower = Math.max(lower, oldLower);
				}
				Long equal = leftElement.getEquality();
				Long upper = leftElement.getUpperBound();
				if(equal == null){
					unionFind.updateElement(leftElement, lower, upper, equal);
				}
			}
		}
		
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

	/**
	 * if the comparison has the form att1 OP att2, put them in the residual list.
	 * if the comparison has the form att OP val, find the element containing
	 * att and update the numeric bound
	 * */
	@Override
	public void visit(MinorThan arg0) {
		// TODO Auto-generated method stub
		Expression left = arg0.getLeftExpression();
		Expression right = arg0.getRightExpression();
		if(left instanceof Column){
			if(right instanceof Column){
				//if they are from the same table, add to select residual list.
				if((((Column) left).getTable()).equals(((Column) right).getTable())){
//					MinorThan minorthan = arg0;
//					minorthan.setLeftExpression(left);
//					minorthan.setRightExpression(right);
//					selectResidual.add(minorthan); 
					selectResidual.add(arg0);
				}else{
					joinResidual.add(arg0);
				}
			}else{
				Element leftElement = unionFind.FindElement(left.toString());
				String rightNum = right.toString();
				Long upper = Long.valueOf(rightNum);
				Long oldUpper = leftElement.getUpperBound();
				if(oldUpper!= null){
					upper = Math.min(upper, oldUpper);
				}
				
				Long equal = leftElement.getEquality();
				Long lower = leftElement.getLowerBound();
				if(equal == null){
					unionFind.updateElement(leftElement, lower, upper, equal);
				}
			}
		}
	}

	/**
	 * if the comparison has the form att1 OP att2, put them in the residual list.
	 * if the comparison has the form att OP val, find the element containing
	 * att and update the numeric bound
	 * */
	@Override
	public void visit(MinorThanEquals arg0) {
		// TODO Auto-generated method stub
		Expression left = arg0.getLeftExpression();
		Expression right = arg0.getRightExpression();
		if(left instanceof Column){
			if(right instanceof Column){
				//if they are from the same table, add to select residual list.
				if((((Column) left).getTable()).equals(((Column) right).getTable())){
//					MinorThan minorthan = arg0;
//					minorthan.setLeftExpression(left);
//					minorthan.setRightExpression(right);
//					selectResidual.add(minorthan); 
					selectResidual.add(arg0);
				}else{
					joinResidual.add(arg0);
				}
			}else{
				Element leftElement = unionFind.FindElement(left.toString());
				String rightNum = right.toString();
				Long upper = Long.valueOf(rightNum);
				upper = upper +1;
				Long oldUpper = leftElement.getUpperBound();
				if(oldUpper!= null){
					upper = Math.min(upper, oldUpper);
				}
				Long equal = leftElement.getEquality();
				Long lower = leftElement.getLowerBound();
				if(equal == null){
					unionFind.updateElement(leftElement, lower, upper, equal);
				}
			}
		}
	}

	/**
	 * Determine whether it should go to the joinResidual list or selectResidual list.
	 * */
	@Override
	public void visit(NotEqualsTo arg0) {
		// TODO Auto-generated method stub
		Expression left = arg0.getLeftExpression();
		Expression right = arg0.getRightExpression();
		if(left instanceof Column){
			if(right instanceof Column){
				if((((Column) left).getTable()).equals(((Column) right).getTable())){
					selectResidual.add(arg0);
				}else{
					joinResidual.add(arg0);
				}
			}else{
				selectResidual.add(arg0);
			}
		}
	}

	@Override
	public void visit(Column arg0) {
		// TODO Auto-generated method stub
		
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
