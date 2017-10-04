package visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import net.sf.jsqlparser.expression.AllComparisonExpression;
import net.sf.jsqlparser.expression.AnyComparisonExpression;
import net.sf.jsqlparser.expression.BinaryExpression;
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
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SubSelect;
import parser.Parser;

/**
 * This class is used to extract join expression from where clause.
 * 
 * @author Lini Tan, lt398; Hao Rong, hr355
 */
public class joinVisitor implements ExpressionVisitor {
	private ArrayList<Expression> join;
	private ArrayList<Expression> select;
	private Boolean hasLong;
	private Stack<Column> cStack;
	
	private ArrayList<String> joinTableName;
	private HashMap<String, ArrayList<Expression>> selectConditionMap;
	private ArrayList<ArrayList<String>> joinPair;
	private HashMap<ArrayList<String>, Expression> joinConditionMap;
	private HashMap<String, String> pairAlias;
	
	public joinVisitor(PlainSelect selectBody){
		hasLong = false;
		cStack = new Stack<Column>();
		select = new ArrayList<Expression>();
		join = new ArrayList<Expression>();
		joinTableName = new ArrayList<String>();
		selectConditionMap = new HashMap<String, ArrayList<Expression>>();
		joinPair = new ArrayList<ArrayList<String>>();
		joinConditionMap = new HashMap<ArrayList<String>, Expression>();
		pairAlias = buildAliasTruePair(selectBody);		
	}
	
	/**This is the method to handle Alias
	 * @return the alias-table hash map
	 * */
	public HashMap<String, String> getPairAlias(){
		return pairAlias;
	}
	
	/**This is the method to handle Alias
	 * @return the alias-table pair hash map
	 * */
	public HashMap<String, String> buildAliasTruePair(PlainSelect selectBody){
		HashMap<String, String> AliasTruePair = new HashMap<String, String>();
		String table = selectBody.getFromItem().toString();
		addPair(AliasTruePair, table);
		
		List<Join> joinPairs = selectBody.getJoins();
		if (joinPairs!=null){
			for(Join singleJoin: joinPairs){
				table = singleJoin.toString();
				addPair(AliasTruePair, table);
			}
		}
		
		return AliasTruePair;
	}
	
	/**This is the method to find alias-table pair
	 * @param the pair to be added
	 * */
	public void addPair(HashMap<String, String> pairSet, String table){
		int index = table.indexOf(" AS ");
		if (index!=-1){ // if there is " AS " in the table
			String original = table.substring(0, index);
			String alias = table.substring(index + 4, table.length());
			pairSet.put(alias, original); // use to substitute alias to table
			pairSet.put(original, original); // use to substitute table to table
		}
		else {
			String original = table;
			pairSet.put(original, original); // use to substitute table to table
		}
	}
	
	/**This is the method to get join expression list
	 * @return the join list
	 * */
	public ArrayList getJoinExpressionList(){
		return join;
	}
	
	/**This is the method to get join table name list
	 * @return the join table name
	 * */
	public ArrayList getJoinTableList(){
		return joinTableName;
	}
	
	/**This is the method to change the select expression list to a hash map
	 * @return the hash map
	 * */
	public HashMap<String, ArrayList<Expression>> getSelectConditionMap(){
		for(Expression e: select){
			int indexDot = e.toString().indexOf(".");
			String tableName = e.toString().substring(0, indexDot);
			tableName = pairAlias.get(tableName);
			ArrayList<Expression> updatedCondition = new ArrayList<Expression>();
			
			if (selectConditionMap.containsKey(tableName)){
				updatedCondition = selectConditionMap.get(tableName);	
			}
			updatedCondition.add(e);
			selectConditionMap.put(tableName, updatedCondition);
		}
		
		return selectConditionMap;
	}
	
	/**This is the method to get join table name pair
	 * @return the list of join pair
	 * */
	public ArrayList getJoinPair(){
		return joinPair;
	}
	
	
	/**This is the method to get the join condition
	 * @return the hash map of join condition
	 * */
	public HashMap<ArrayList<String>, Expression> getJoinConditionMap(){
		return joinConditionMap;
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
		arg0.getLeftExpression().accept(this);
		arg0.getRightExpression().accept(this);
		Expression left = arg0.getLeftExpression();
		Expression right = arg0.getRightExpression();
		EqualsTo e = new EqualsTo();
		e.setLeftExpression(left);
		e.setRightExpression(right);
//		 System.out.println(e.toString());
//		 System.out.println(hasLong);
		if(hasLong == true){
//			 System.out.println("yoyo");	
		 select.add(e);
		 
		 hasLong = false;
		}else{
//			System.out.println("yoy");
			Column r = cStack.pop();
//			System.out.println("yo");
			Column l  = cStack.pop();	
//			System.out.println("y");
			if(r.getTable().getName().equals(l.getTable().getName())){
				select.add(e);
			}else{
				join.add(e);
				if (!joinTableName.contains(pairAlias.get(l.getTable().getName()))){
					joinTableName.add(pairAlias.get(l.getTable().getName()));
				}
				if (!joinTableName.contains(pairAlias.get(r.getTable().getName()))){
					joinTableName.add(pairAlias.get(r.getTable().getName()));
				}
				
				ArrayList<String> newPair = new ArrayList<String>();
				newPair.add(pairAlias.get(l.getTable().getName()));
				newPair.add(pairAlias.get(r.getTable().getName()));
				joinPair.add(newPair);
				
				joinConditionMap.put(newPair, e);
			}
		}
		
	}

	/**This is the method when visit GreaterThan Expression
	 * @param GreaterThan Expression that to be visited
	 * */
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
				if (!joinTableName.contains(pairAlias.get(l.getTable().getName()))){
					joinTableName.add(pairAlias.get(l.getTable().getName()));
				}
				if (!joinTableName.contains(pairAlias.get(r.getTable().getName()))){
					joinTableName.add(pairAlias.get(r.getTable().getName()));
				}
				
				ArrayList<String> newPair = new ArrayList<String>();
				newPair.add(pairAlias.get(l.getTable().getName()));
				newPair.add(pairAlias.get(r.getTable().getName()));
				joinPair.add(newPair);
				
				joinConditionMap.put(newPair, e);
			}
		}
	}
	
	/**This is the method when visit GreaterThanEquals Expression
	 * @param GreaterThanEquals Expression that to be visited
	 * */
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
				if (!joinTableName.contains(pairAlias.get(l.getTable().getName()))){
					joinTableName.add(pairAlias.get(l.getTable().getName()));
				}
				if (!joinTableName.contains(pairAlias.get(r.getTable().getName()))){
					joinTableName.add(pairAlias.get(r.getTable().getName()));
				}
				
				ArrayList<String> newPair = new ArrayList<String>();
				newPair.add(pairAlias.get(l.getTable().getName()));
				newPair.add(pairAlias.get(r.getTable().getName()));
				joinPair.add(newPair);
				
				joinConditionMap.put(newPair, e);
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

	/**This is the method when visit MinorThan Expression
	 * @param MinorThan Expression that to be visited
	 * */
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
//				System.out.println("hh");
				select.add(e);
			}else{
				join.add(e);
				if (!joinTableName.contains(pairAlias.get(l.getTable().getName()))){
					joinTableName.add(pairAlias.get(l.getTable().getName()));
				}
				if (!joinTableName.contains(pairAlias.get(r.getTable().getName()))){
					joinTableName.add(pairAlias.get(r.getTable().getName()));
				}
				
				ArrayList<String> newPair = new ArrayList<String>();
				newPair.add(pairAlias.get(l.getTable().getName()));
				newPair.add(pairAlias.get(r.getTable().getName()));
				joinPair.add(newPair);
				
				joinConditionMap.put(newPair, e);
			}
		}
		
	}

	/**This is the method when visit MinorThanEquals Expression
	 * @param MinorThanEquals Expression that to be visited
	 * */
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
				if (!joinTableName.contains(pairAlias.get(l.getTable().getName()))){
					joinTableName.add(pairAlias.get(l.getTable().getName()));
				}
				if (!joinTableName.contains(pairAlias.get(r.getTable().getName()))){
					joinTableName.add(pairAlias.get(r.getTable().getName()));
				}
				
				ArrayList<String> newPair = new ArrayList<String>();
				newPair.add(pairAlias.get(l.getTable().getName()));
				newPair.add(pairAlias.get(r.getTable().getName()));
				joinPair.add(newPair);
				
				joinConditionMap.put(newPair, e);
			}
		}
		
	}

	/**This is the method when visit NotEqualsTo Expression
	 * @param NotEqualsTo Expression that to be visited
	 * */
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
				if (!joinTableName.contains(pairAlias.get(l.getTable().getName()))){
					joinTableName.add(pairAlias.get(l.getTable().getName()));
				}
				if (!joinTableName.contains(pairAlias.get(r.getTable().getName()))){
					joinTableName.add(pairAlias.get(r.getTable().getName()));
				}
				
				ArrayList<String> newPair = new ArrayList<String>();
				newPair.add(pairAlias.get(l.getTable().getName()));
				newPair.add(pairAlias.get(r.getTable().getName()));
				joinPair.add(newPair);
				
				joinConditionMap.put(newPair, e);
			}
		}
		
	}

	/**This is the method when visit column Expression
	 * @param column Expression that to be visited
	 * */
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

//	
//	 public static void main(String args[])throws Exception{  
//			//long a, equlas e, colum c d g, c=d
//
//		 
//		 	String querypath = "/Users/LukerRong/Desktop/CS5321/joinTest.sql";
//			Parser p = new Parser(querypath);
//			ArrayList<PlainSelect> queryList = p.getQueryList();
//			
//			PlainSelect s = queryList.get(0);
//			System.out.println(s);
//			
//			Expression e = s.getWhere(); 
//			System.out.println(e.toString());
//			joinVisitor j = new joinVisitor(s);
//			e.accept(j);
//			
//			System.out.println(j.getJoinExpressionList().toString());
//			System.out.println(j.getJoinTableList().toString());
//			System.out.println(j.getSelectConditionMap().toString());
//			System.out.println(j.getJoinPair().toString());
//			System.out.println(j.getJoinConditionMap().toString());
//	 } 


}
