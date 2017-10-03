package Operator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import Database_Catalog.Catalog;
import Tuple.Tuple;
import net.sf.jsqlparser.statement.select.PlainSelect;

public class DuplicateEliminationOperators extends Operator {
	
	Operator childOp;
	HashSet distinctTuple;
	
	public DuplicateEliminationOperators(Operator op){
		childOp = op;
		distinctTuple = new HashSet<Tuple>();
		
	}
	
	@Override
	public Tuple getNextTuple() {
		// TODO Auto-generated method stub
		Tuple a = childOp.getNextTuple();
		while(a!=null){
		//if(distinctTuple.isEmpty()) distinctTuple.add(a);
		if(!distinctTuple.contains(a)){
			System.out.println("hh");
			distinctTuple.add(a);
			return a;
		}
		a=childOp.getNextTuple();
		}
		return null;
	}
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		childOp.reset();
	}
	@Override
	public void dump() {
		// TODO Auto-generated method stub
		Tuple a =getNextTuple();
		while(a != null){
			System.out.println(a.getTuple());
			a =getNextTuple();
		}
	}
	
	@Override
	public ArrayList<Tuple> writeToFile() {
		// TODO Auto-generated method stub
		Tuple a =getNextTuple();
		ArrayList<Tuple> result = new ArrayList<Tuple>();
		while(a!= null){
			result.add(a);
			a =getNextTuple();
		}
		return result;
	}
}
