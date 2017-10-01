package Operator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

import Database_Catalog.Catalog;
import Tuple.Tuple;
import net.sf.jsqlparser.statement.select.PlainSelect;
import visitor.visitor;
import net.sf.jsqlparser.expression.Expression;

public class JoinOperator extends Operator {
	
	Expression expression;
	Operator outter;
	Operator inner;
	
	public JoinOperator(Operator left, Operator right, Expression joinExpression) {
		// TODO Auto-generated constructor stub
		expression = joinExpression;
		outter = left;
		inner = right;
	}
	
	@Override
	public Tuple getNextTuple() {
		// TODO Auto-generated method stub
		Tuple a = outter.getNextTuple();
		while(a!=null){
			Tuple b = inner.getNextTuple();
			while(b!=null){
				ArrayList alist = a.getTuple();
				ArrayList blist = b.getTuple();
				String s = "";
				for(int i=0; i<alist.size(); i++){
					s=s+alist.get(i)+",";
				}
				for(int j=0; j<alist.size(); j++){
					s=s+blist.get(j)+",";
				}
				s = s.substring(0, s.length()-1);
				Tuple tt = new Tuple(s);
				// build new visitor
				visitor v = new visitor(tt);
				expression.accept(v);
				if(v.getResult()){
					return tt;
				}
				b = inner.getNextTuple();
			}
//inner is null
			inner.reset();
			a = outter.getNextTuple();
		}
		return null;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		inner.reset();
		outter.reset();		
	}

	@Override
	public void dump() {
		// TODO Auto-generated method stub
		Tuple a =new Tuple("");
		while((a=getNextTuple()) != null){
			System.out.println(a.getTuple());
			//System.out.println(a.toString());
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
