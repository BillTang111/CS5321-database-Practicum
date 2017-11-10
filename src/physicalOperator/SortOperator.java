package physicalOperator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Collections;


import Database_Catalog.Catalog;
import Tuple.Tuple;
import Tuple.TupleComparator;
import net.sf.jsqlparser.statement.select.PlainSelect;
import visitor.printQueryPlanVisitor;

/**
 * This class is used when sql query contains order by condition.
 * 
 * @author Lini Tan, lt398
 */
public class SortOperator extends Operator {
	
	Operator childOp;
	LinkedList<Tuple> sorted;
	List order;
	HashMap<String,String> PairAlias;
	int index;
	
	//public SortOperator(Operator op, List orderList) {
	public SortOperator(Operator op, PlainSelect selectBody) {
		// TODO Auto-generated constructor stub
		//System.out.println(orderList.toString());
		childOp = op;
		sorted = new LinkedList<Tuple>();
		order = selectBody.getOrderByElements();
		Catalog catalog = Catalog.getInstance();
		PairAlias = catalog.getPairAlias();
		index = 0;
		BuildList();
		
	}
	
	/**constructor for sort operator which take in orderlist as input*/
	public SortOperator(Operator op, List orderList) {
		// TODO Auto-generated constructor stub
		//System.out.println(orderList.toString());
		childOp = op;
		sorted = new LinkedList<Tuple>();
		order = orderList;
		Catalog catalog = Catalog.getInstance();
		PairAlias = catalog.getPairAlias();
		index = 0;
		BuildList();
		
	}
	
	/**This method grab all tuple from the child operator and add in the list*/
	public void BuildList(){
		Tuple a = childOp.getNextTuple();
		while(a!=null){
			//add all tuples into list
			sorted.add(a);
			if(order==null){
				
				
				Object[] mapKeySet = a.getTupleMap().keySet().toArray();
				order = new ArrayList();
				for(int i=mapKeySet.length-1; i>=0; i--){
					//System.out.println(key.toString());
					order.add(mapKeySet[i].toString());
					
				}
				
				//System.out.println(order);
			}
			a=childOp.getNextTuple();
			//System.out.println(a.getTuple().toString());
			
		}
		
		//System.out.println("hh"+order.toString());
		Collections.sort(sorted, new TupleComparator(order));
	}
	
	/** This method return the satisfied tuple and get next tuples.
	 * @return the next tuple 
	 * */
	@Override
	public Tuple getNextTuple() {
		// TODO Auto-generated method stub
		//System.out.println("yy");
		if(sorted.size()!=0){ 
			index ++;
			if(index-1<sorted.size()) return sorted.get(index-1);
			//return (Tuple) sorted.pop();
		}
		return null;
	}

	/**Reset the operator to re-call from the beginning */
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		index = 0;
		childOp.reset();
	}

	/**To print your result. Use for debug 
	 * @param printOrNot: 0: don't print, 1: print*/
	@Override
	public void dump(int printOrNot) {
		// TODO Auto-generated method stub
		Tuple a =getNextTuple();
		if (printOrNot==1){
			while(a != null){
				System.out.println(a.getTuple());
				a=getNextTuple();
				//System.out.println(a.toString());
			}
		} 
		else if (printOrNot==0){
			while(a != null){
				a=getNextTuple();
			}
		}
	}
	
	/** Get all the result tuple in this operator (For debugging) 
	 * @return a list of tuple
	 */
	@Override
	public ArrayList<Tuple> getAllTuple() {
		// TODO Auto-generated method stub
		//BuildList();
		Tuple a =getNextTuple();
		ArrayList<Tuple> result = new ArrayList<Tuple>();
		while(a!= null){
			result.add(a);
			a =getNextTuple();
		}
		return result;
	}

	@Override
	public void reset(int index) {
		// TODO Auto-generated method stub
		this.index = index;
		
	}

	@Override
	public int getIndex() {
		// TODO Auto-generated method stub
		return index;
	}

	@Override
	public void accept(printQueryPlanVisitor printQueryPlanVisitor) {
		printQueryPlanVisitor.visit(this);
	}

	public Operator getChild() {
		return this.childOp;
	}
}

///**
// * This class set up the tuple comparator
// * 
// * @author Lini Tan, lt398
// */
//public class TupleComparator implements Comparator<Tuple> {
//	List condition;
//	HashMap<String,String> PairAlias;
//	
//	public TupleComparator(List order){
//		condition = order;
//		Catalog catalog = Catalog.getInstance();
//		PairAlias = catalog.getPairAlias();
//	}
//	
//	/**compare two tuples.
//	 * 
//	 * @param two tuples to be compared
//	 * */
//    @Override
//    public int compare(Tuple a, Tuple b) {
//    	HashMap amap = a.getTupleMap();
//    	ArrayList alist = a.getTuple();
//    	ArrayList blist = b.getTuple();
//    	HashSet s = new HashSet<String>();
//    	
//    	
//    	//compare by condition
//    	if(condition!=null){
//    	//System.out.println(condition.toString());
//    	for(int i=0; i<condition.size(); i++){
//    		//System.out.println(condition.get(i).toString());
//    		//System.out.println(amap.get(condition.get(i).toString()));
//    		
//    		String aliasCondition = condition.get(i).toString();
//    		int dot = aliasCondition.indexOf(".");
//    		String aliasT = aliasCondition.substring(0, dot);
//    		aliasCondition = PairAlias.get(aliasT) + "." + aliasCondition.substring(dot+1, aliasCondition.length());
//    		
//    		
//    		//System.out.println(aliasCondition);
//    		int index = (int) amap.get(aliasCondition);
//    		int anum = Integer.parseInt((String) alist.get(index));
//    		//System.out.println("a "+anum);
//    		
//    		int bnum = Integer.parseInt((String) blist.get(index));
//    		//System.out.println("b "+bnum);
//    		if(anum < bnum) return -1;
//    		if(anum > bnum) return 1;
//    		s.add(aliasCondition);
//    	}
//    	}
//    	//if not condition or condition are the same, compare tuple from left to right.
//    	for(int i=0; i<alist.size(); i++){
//    		if(Integer.parseInt((String)alist.get(i))<Integer.parseInt((String)blist.get(i))){
//    			return -1;
//    		}else if(Integer.parseInt((String)alist.get(i))>Integer.parseInt((String)blist.get(i))){
//    			return 1;
//    		}
//    	}
//    	return 0;
//    	
//  //  	ArrayList field = new ArrayList();
////    	List table = a.getNameList();
////    	//System.out.println("a.getNameList()" + table);
////    	
////    	
////    	Catalog c = Catalog.getInstance();
////    	for(int j=0; j<table.size(); j++){
////    		ArrayList tfield = (ArrayList) c.getSchema().get(table.get(j));
////    		for(int i=0; i<tfield.size(); i++){
////    			
////    			field.add(table.get(j).toString()+"."+tfield.get(i));
////    		}
////    	}
//    	
////    	HashMap tuplemap = a.getTupleMap();
////    	Object[] keySet = tuplemap.keySet().toArray();
////    	for(int i = keySet.length-1; i>=0; i-- ){
////    		field.add(keySet[i]);
////    	}
////    	//System.out.println(x);
////    	
////    	//System.out.println("amap " + amap);
////    	//System.out.println(field.toString());
////    	for(int i=0; i<field.size()-1; i++){
////    		if(s.contains(field.get(i).toString())) continue;
////    		if(amap.get(field.get(i))==null) continue;
////    		//System.out.println(field.get(i));
////    		int index = (int) amap.get(field.get(i));
////    		//System.out.println(index);
////    		int anum = Integer.parseInt((String) alist.get(index));
////    		//System.out.println("a "+anum);
////    		int bnum = Integer.parseInt((String) blist.get(index));
////    		//System.out.println("b "+bnum);
////    		if(anum < bnum) {
////    			//System.out.println("haha");
////    			return -1;}
////    		if(anum > bnum) return 1;
////    		//s.add(field.get(i));
////    	}
////        return 0;
//    }
