package Operator;

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
import visitor.visitor;

public class SelectOperator extends Operator {
	
//	PlainSelect select; //Store the plainSelect object parsed from query
	Expression e;
//	BufferedReader br;
//	FromItem input;
//	String location;
	Operator childOp;
	
	public SelectOperator(Expression singleSelect, Operator op) throws IOException {
		// TODO Auto-generated constructor stub
//		input = selectBody.getFromItem();
//		location = Catalog.getInstance().getInputLocation();	
//		br = new BufferedReader(new FileReader(location + "/db/data/"+input));  
//		select = selectBody;
//		e = selectBody.getWhere();
		e = singleSelect;
		childOp = op;
	}

	@Override
	public Tuple getNextTuple() {
		Tuple a = childOp.getNextTuple();
		while(a!=null){
			visitor v = new visitor(a);
//			if(e==null){ 
//				System.out.println("condition is null");
//				return a;
//				}
			e.accept(v);
			if(v.getResult()){
				return a;
			}
			a = childOp.getNextTuple();
		}
		return null;
	}	

	@Override
	public void reset() {
		childOp.reset();
	}

	@Override
	public void dump() {
		// TODO Auto-generated method stub
		Tuple a =new Tuple("");
		while((a=getNextTuple()) != null){
			System.out.println(a.getTuple());
		}
		
	}
	
	@Override
	public ArrayList<Tuple> writeToFile() {
		// TODO Auto-generated method stub
		Tuple a =new Tuple("");
		ArrayList<Tuple> result = new ArrayList<Tuple>();
		while((a=getNextTuple()) != null){
			result.add(a);
		}
		return result;
	}



}
