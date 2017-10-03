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
	LinkedList<Tuple> sorted;
	List order;
	
	
	public SortOperator(Operator op, PlainSelect selectBody) {
		// TODO Auto-generated constructor stub
		childOp = op;
		sorted = new LinkedList<Tuple>();
		order = selectBody.getOrderByElements();

		
	}
	
	public void BuildList(){
		Tuple a = childOp.getNextTuple();
		while(a!=null){
			//add all tuples into list
			sorted.add(a);
			a=childOp.getNextTuple();
			//System.out.println(a.getTuple().toString());
		}
		
		Collections.sort(sorted, new TupleComparator(order));
	}
	
	@Override
	public Tuple getNextTuple() {
		// TODO Auto-generated method stub
		if(sorted.size()!=0) return (Tuple) sorted.pop();
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
		BuildList();
		Tuple a =getNextTuple();
		while(a != null){
			System.out.println(a.getTuple());
			a =getNextTuple();
		}
		
	}
	
	@Override
	public ArrayList<Tuple> writeToFile() {
		// TODO Auto-generated method stub
		BuildList();
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
    	HashSet s = new HashSet<String>();
    	//compare by condition
    	
    	for(int i=0; i<condition.size(); i++){
    		//System.out.println(amap.get(condition.get(i).toString()));
    		int index = (int) amap.get(condition.get(i).toString());
    		int anum = Integer.parseInt((String) alist.get(index));
    		//System.out.println("a "+anum);
    		
    		int bnum = Integer.parseInt((String) blist.get(index));
    	//	System.out.println("b "+bnum);
    		if(anum < bnum) return -1;
    		if(anum > bnum) return 1;
    		s.add(condition.get(i).toString());
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
    	//System.out.println(field.toString());
    	for(int i=0; i<field.size(); i++){
    		if(s.contains(field.get(i).toString())) continue;
    		int index = (int) amap.get(field.get(i));
    		//System.out.println(index);
    		int anum = Integer.parseInt((String) alist.get(index));
    		//System.out.println("a "+anum);
    		int bnum = Integer.parseInt((String) blist.get(index));
    		//System.out.println("b "+bnum);
    		if(anum < bnum) {
    			System.out.println("haha");
    			return -1;}
    		if(anum > bnum) return 1;
    		//s.add(field.get(i));
    	}
        return 0;
    }
}