package Operator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Collections;


import Database_Catalog.Catalog;
import Tuple.Tuple;
import net.sf.jsqlparser.statement.select.PlainSelect;

public class SortOperator extends Operator {
	
	Operator childOp;
	LinkedList sorted;
	List order;
	
	
	public SortOperator(Operator op, PlainSelect selectBody) {
		// TODO Auto-generated constructor stub
		childOp = op;
		sorted = new LinkedList();
		order = selectBody.getOrderByElements();
		
	}

	@Override
	public Tuple getNextTuple() {
		// TODO Auto-generated method stub
		Tuple a = childOp.getNextTuple();
		while(a!=null){
			//add all tuples into list
			sorted.add(a);
			a=childOp.getNextTuple();
		}
		
		Collections.sort(sorted, new TupleComparator(order));
		
		return (Tuple) sorted.pop();
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

class TupleComparator implements Comparator<Tuple> {
	List condition;
	
	public TupleComparator(List order){
		condition = order;
	}
	
    @Override
    public int compare(Tuple a, Tuple b) {
    	HashMap amap = a.getTupleMap();
    	ArrayList alist = a.getTuple();
    	ArrayList blist = b.getTuple();
    	HashSet s = new HashSet();
    	//compare by condition
    	for(int i=0; i<condition.size(); i++){
    		int index = (int) amap.get(condition.get(i));
    		int anum = Integer.parseInt((String) alist.get(index));
    		int bnum = Integer.parseInt((String) alist.get(index));
    		if(anum < bnum) return -1;
    		if(anum > bnum) return 1;
    		s.add(condition.get(i));
    	}
    	ArrayList field = new ArrayList();
    	List table = a.getNameList();
    	Catalog c = Catalog.getInstance();
    	for(int j=0; j<table.size(); j++){
    		ArrayList tfield = (ArrayList) c.getSchema().get(table.get(j));
    		for(int i=0; i<tfield.size(); i++){
    			field.add(table.get(j).toString()+"."+tfield.get(i));
    		}
    	}
    	for(int i=0; i<field.size(); i++){
    		if(s.contains(field.get(i))) continue;
    		int index = (int) amap.get(field.get(i));
    		int anum = Integer.parseInt((String) alist.get(index));
    		int bnum = Integer.parseInt((String) alist.get(index));
    		if(anum < bnum) return -1;
    		if(anum > bnum) return 1;
    		s.add(field.get(i));
    	}
        return 0;
    }
}