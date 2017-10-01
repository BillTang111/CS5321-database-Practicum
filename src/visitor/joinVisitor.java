package visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

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
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SubSelect;
import parser.Parser;

public class joinVisitor implements ExpressionVisitor {
	private ArrayList<Expression> join;
	private ArrayList<Expression> select;
	private Boolean hasLong;
	private Stack<Column> cStack;
	
	private ArrayList<String> joinTableName;
	private HashMap<String, ArrayList<Expression>> hashedSelect;
	
	
	public joinVisitor(){
		hasLong = false;
		cStack = new Stack<Column>();
		select = new ArrayList<Expression>();
		join = new ArrayList<Expression>();
		joinTableName = new ArrayList<String>();
		hashedSelect = new HashMap<String, ArrayList<Expression>>();
	}
	
	public ArrayList getJoinExpressionList(){
		return join;
	}
	
	public ArrayList getJoinTableList(){
		return joinTableName;
	}
	
	public ArrayList getSelectExpressionList(){
		return select;
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
		hasLong=true;
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

	@Override
	public void visit(EqualsTo arg0) {
		// TODO Auto-generated method stub
		arg0.getLeftExpression().accept(this);
		arg0.getRightExpression().accept(this);
		Expression left = arg0.getLeftExpression();
		 Expression right = arg0.getRightExpression();
		 EqualsTo e = new EqualsTo();
		 e.setLeftExpression(left);
		 e.setRightExpression(right);
		 System.out.println(e.toString());
		 System.out.println(hasLong);
		if(hasLong == true){
			 System.out.println("yoyo");	
		 select.add(e);
		 
		 hasLong = false;
		}else{
			System.out.println("yoy");
			Column r = cStack.pop();
			System.out.println("yo");
			Column l  = cStack.pop();	
			System.out.println("y");
			if(r.getTable().getName().equals(l.getTable().getName())){
				select.add(e);
			}else{
				join.add(e);
				if (!joinTableName.contains(l.getTable().getName())){
					joinTableName.add(l.getTable().getName());
				}
				if (!joinTableName.contains(r.getTable().getName())){
					joinTableName.add(r.getTable().getName());
				}
			}
		}
		
	}

	@Override
	public void visit(GreaterThan arg0) {
		// TODO Auto-generated method stub
		arg0.getLeftExpression().accept(this);
		arg0.getRightExpression().accept(this);
		Expression left = arg0.getLeftExpression();
		 Expression right = arg0.getRightExpression();
		 GreaterThan e = new GreaterThan();
		 e.setLeftExpression(left);
		 e.setRightExpression(right);
		if(hasLong == true){
		 select.add(e);
		 hasLong = false;
		}else{
			Column r = cStack.pop();
			Column l  = cStack.pop();
			if(r.getTable().getName().equals(l.getTable().getName())){
				select.add(e);
			}else{
				join.add(e);
				if (!joinTableName.contains(l.getTable().getName())){
					joinTableName.add(l.getTable().getName());
				}
				if (!joinTableName.contains(r.getTable().getName())){
					joinTableName.add(r.getTable().getName());
				}
			}
		}
	}

	@Override
	public void visit(GreaterThanEquals arg0) {
		// TODO Auto-generated method stub
		arg0.getLeftExpression().accept(this);
		arg0.getRightExpression().accept(this);
		Expression left = arg0.getLeftExpression();
		 Expression right = arg0.getRightExpression();
		 GreaterThanEquals e = new GreaterThanEquals();
		 e.setLeftExpression(left);
		 e.setRightExpression(right);
		if(hasLong == true){
		 select.add(e);
		 hasLong = false;
		}else{
			Column r = cStack.pop();
			Column l  = cStack.pop();
			if(r.getTable().getName().equals(l.getTable().getName())){
				select.add(e);
			}else{
				join.add(e);
				if (!joinTableName.contains(l.getTable().getName())){
					joinTableName.add(l.getTable().getName());
				}
				if (!joinTableName.contains(r.getTable().getName())){
					joinTableName.add(r.getTable().getName());
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

	@Override
	public void visit(MinorThan arg0) {
		// TODO Auto-generated method stub
		arg0.getLeftExpression().accept(this);
		arg0.getRightExpression().accept(this);
		Expression left = arg0.getLeftExpression();
		 Expression right = arg0.getRightExpression();
		 MinorThan e = new MinorThan();
		 e.setLeftExpression(left);
		 e.setRightExpression(right);
		if(hasLong == true){
		 select.add(e);
		 hasLong = false;
		}else{
			Column r = cStack.pop();
			Column l  = cStack.pop();
			if(r.getTable().getName().equals(l.getTable().getName())){
				System.out.println("hh");
				select.add(e);
			}else{
				join.add(e);
				if (!joinTableName.contains(l.getTable().getName())){
					joinTableName.add(l.getTable().getName());
				}
				if (!joinTableName.contains(r.getTable().getName())){
					joinTableName.add(r.getTable().getName());
				}
			}
		}
		
	}

	@Override
	public void visit(MinorThanEquals arg0) {
		// TODO Auto-generated method stub
		arg0.getLeftExpression().accept(this);
		arg0.getRightExpression().accept(this);
		Expression left = arg0.getLeftExpression();
		 Expression right = arg0.getRightExpression();
		 MinorThanEquals e = new MinorThanEquals();
		 e.setLeftExpression(left);
		 e.setRightExpression(right);
		if(hasLong == true){
		 select.add(e);
		 hasLong = false;
		}else{
			Column r = cStack.pop();
			Column l  = cStack.pop();
			if(r.getTable().getName().equals(l.getTable().getName())){
				select.add(e);
			}else{
				join.add(e);
				if (!joinTableName.contains(l.getTable().getName())){
					joinTableName.add(l.getTable().getName());
				}
				if (!joinTableName.contains(r.getTable().getName())){
					joinTableName.add(r.getTable().getName());
				}
			}
		}
		
	}

	@Override
	public void visit(NotEqualsTo arg0) {
		// TODO Auto-generated method stub
		arg0.getLeftExpression().accept(this);
		arg0.getRightExpression().accept(this);
		Expression left = arg0.getLeftExpression();
		 Expression right = arg0.getRightExpression();
		 NotEqualsTo e = new NotEqualsTo();
		 e.setLeftExpression(left);
		 e.setRightExpression(right);
		if(hasLong == true){
		 select.add(e);
		 hasLong = false;
		}else{
			Column r = cStack.pop();
			Column l  = cStack.pop();
			if(r.getTable().getName().equals(l.getTable().getName())){
				select.add(e);
			}else{
				join.add(e);
				if (!joinTableName.contains(l.getTable().getName())){
					joinTableName.add(l.getTable().getName());
				}
				if (!joinTableName.contains(r.getTable().getName())){
					joinTableName.add(r.getTable().getName());
				}
			}
		}
		
	}

	@Override
	public void visit(Column arg0) {
		// TODO Auto-generated method stub
		cStack.push(arg0);
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
	
	 public static void main(String args[])throws Exception{  
			//long a, equlas e, colum c d g, c=d

		 
		 	String querypath = "/Users/LukerRong/Desktop/CS5321/joinTest.sql";
			Parser p = new Parser(querypath);
			ArrayList<PlainSelect> queryList = p.getQueryList();
			
			PlainSelect s = queryList.get(0);
			System.out.println(s);
			
			Expression e = s.getWhere(); 
			System.out.println(e.toString());
			joinVisitor j = new joinVisitor();
			e.accept(j);
			
			System.out.println(j.getJoinExpressionList().toString());
			System.out.println(j.getJoinTableList().toString());
			System.out.println(j.getSelectExpressionList().toString());

	 } 

}
