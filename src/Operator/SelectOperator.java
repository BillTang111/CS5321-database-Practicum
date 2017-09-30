package Operator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import Database_Catalog.Catalog;
import Tuple.Tuple;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.PlainSelect;
import visitor.visitor;

public class SelectOperator extends Operator {
	
	PlainSelect select; //Store the plainSelect object parsed from query
	Expression e;
	BufferedReader br;
	FromItem input;
	String location;
	ScanOperator scan;
	
	public SelectOperator(PlainSelect selectBody) throws IOException {
		// TODO Auto-generated constructor stub
		input = selectBody.getFromItem();
		location = Catalog.getInstance().getInputLocation();	
		br = new BufferedReader(new FileReader(location + "/db/data/"+input));  
		select = selectBody;
		e = selectBody.getWhere();
		scan = new ScanOperator(select);
	}

	@Override
	public Tuple getNextTuple() {
		Tuple a = scan.getNextTuple();
		if(a!=null){
		visitor v = new visitor(a);
		if(e==null){ 
			System.out.println("condition is null");
			return a;}
		e.accept(v);
		if(v.getResult()){
			return a;
		}
		}
		return null;
		
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		try {
			br = new BufferedReader(new FileReader(location + "/db/data/"+input+".txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}

	@Override
	public void dump() {
		// TODO Auto-generated method stub
		Tuple a =new Tuple("");
		while((a=getNextTuple()) != null){
			System.out.println(a.getTuple());
		}
		
	}



}
