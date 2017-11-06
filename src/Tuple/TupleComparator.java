package Tuple;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import Database_Catalog.Catalog;

/**
 * This class set up the tuple comparator
 * 
 * @author Lini Tan, lt398
 */
public class TupleComparator implements Comparator<Tuple> {
	List condition;
	HashMap<String,String> PairAlias;
	
	public TupleComparator(List order){
		condition = order;
		Catalog catalog = Catalog.getInstance();
		PairAlias = catalog.getPairAlias();
		System.out.println("if any? "+PairAlias.size());
	}
	
	/**compare two tuples.
	 * 
	 * @param two tuples to be compared
	 * */
    @Override
    public int compare(Tuple a, Tuple b) {
    	HashMap amap = a.getTupleMap();
    	ArrayList alist = a.getTuple();
    	ArrayList blist = b.getTuple();
    	HashSet s = new HashSet<String>();
    	
    	
    	//compare by condition
    	if(condition!=null){
    	//System.out.println(condition.toString());
    	for(int i=0; i<condition.size(); i++){
    		//System.out.println(condition.get(i).toString());
    		//System.out.println(amap.get(condition.get(i).toString()));
    		
    		String aliasCondition = condition.get(i).toString();
    		int dot = aliasCondition.indexOf(".");
    		String aliasT = aliasCondition.substring(0, dot);
    		aliasCondition = PairAlias.get(aliasT) + "." + aliasCondition.substring(dot+1, aliasCondition.length());
    		
    		
    		//System.out.println(aliasCondition);
    		int index = (int) amap.get(aliasCondition);
    		int anum = Integer.parseInt((String) alist.get(index));
    		//System.out.println("a "+anum);
    		
    		int bnum = Integer.parseInt((String) blist.get(index));
    		//System.out.println("b "+bnum);
    		if(anum < bnum) return -1;
    		if(anum > bnum) return 1;
    		s.add(aliasCondition);
    	}
    	}
    	//if not condition or condition are the same, compare tuple from left to right.
    	for(int i=0; i<alist.size(); i++){
    		if(Integer.parseInt((String)alist.get(i))<Integer.parseInt((String)blist.get(i))){
    			return -1;
    		}else if(Integer.parseInt((String)alist.get(i))>Integer.parseInt((String)blist.get(i))){
    			return 1;
    		}
    	}
    	return 0;
    	
  //  	ArrayList field = new ArrayList();
//    	List table = a.getNameList();
//    	//System.out.println("a.getNameList()" + table);
//    	
//    	
//    	Catalog c = Catalog.getInstance();
//    	for(int j=0; j<table.size(); j++){
//    		ArrayList tfield = (ArrayList) c.getSchema().get(table.get(j));
//    		for(int i=0; i<tfield.size(); i++){
//    			
//    			field.add(table.get(j).toString()+"."+tfield.get(i));
//    		}
//    	}
    	
//    	HashMap tuplemap = a.getTupleMap();
//    	Object[] keySet = tuplemap.keySet().toArray();
//    	for(int i = keySet.length-1; i>=0; i-- ){
//    		field.add(keySet[i]);
//    	}
//    	//System.out.println(x);
//    	
//    	//System.out.println("amap " + amap);
//    	//System.out.println(field.toString());
//    	for(int i=0; i<field.size()-1; i++){
//    		if(s.contains(field.get(i).toString())) continue;
//    		if(amap.get(field.get(i))==null) continue;
//    		//System.out.println(field.get(i));
//    		int index = (int) amap.get(field.get(i));
//    		//System.out.println(index);
//    		int anum = Integer.parseInt((String) alist.get(index));
//    		//System.out.println("a "+anum);
//    		int bnum = Integer.parseInt((String) blist.get(index));
//    		//System.out.println("b "+bnum);
//    		if(anum < bnum) {
//    			//System.out.println("haha");
//    			return -1;}
//    		if(anum > bnum) return 1;
//    		//s.add(field.get(i));
//    	}
//        return 0;
    }
}
