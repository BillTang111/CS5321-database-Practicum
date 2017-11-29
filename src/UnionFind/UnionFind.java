package UnionFind;

import java.util.ArrayList;
import java.util.HashSet;

import net.sf.jsqlparser.schema.Column;

/**
 * This class in the union find which store a collection of elements
 * 
 * Author: Lini Tan lt398
 * */
public class UnionFind {
	private ArrayList<Element> unionFind;
	
	public UnionFind(){
		unionFind = new ArrayList<Element>();
	}
	
	/**
	 * given a particular attribute,
	 * such element is found, create it and return it. nd and return the union-nd element containing that attribute; if no
	 * */
	public Element FindElement(Column attr){
		//loop the list of union find to find the element contains attr
		
		for(Element e: unionFind){
			if(e.getattri().contains(attr)){
				return e;
			}
		}
		HashSet<Column> attrList = new HashSet<Column>();
		attrList.add(attr);
		return new Element(attrList, null, null, null);
	}
	
	/**
	 * Given two union-find elements. modify the union-find data structure
	 * so that these two elements get unioned.
	 * */
	public void merge(Element e1, Element e2){
		//build new element
			//build new attrList
		HashSet<Column> e1Attr = e1.getattri();
		HashSet<Column> e2Attr = e2.getattri();
		HashSet<Column> mergeAttr = new HashSet<Column>();
		
		for(Column s: e1Attr){
			mergeAttr.add(s);
		}
		
		for(Column s: e2Attr){
			mergeAttr.add(s);
		}
		
		//build constraint
		Long e1Equal = e1.getEquality();
		Long e2Equal = e2.getEquality();
		Long mergeEqual = null;
		Long mergeUpper = null;
		Long mergeLower = null;
		if(e1Equal != null || e2Equal != null){
			if(e1Equal != null){
				mergeEqual = e1Equal;
				mergeUpper = e1Equal;
				mergeLower = e1Equal;
			} 
			if(e2Equal != null){
				mergeEqual = e2Equal;
				mergeUpper = e2Equal;
				mergeLower = e2Equal;
			}
		}else{
			Long e1Upper = e1.getUpperBound();
			Long e2Upper = e2.getUpperBound();
			if(e1Upper == null){
				mergeUpper = e2Upper;
			}else if (e2Upper == null){
				mergeUpper = e1Upper;
			}else{
				mergeUpper = Math.min(e1Upper, e2Upper);
			}
			
			Long e1Lower = e1.getLowerBound();
			Long e2Lower = e2.getLowerBound();
			if(e1Lower == null){
				mergeLower = e2Lower;
			}else if (e2Lower == null){
				mergeLower = e1Lower;
			}else{
				mergeLower = Math.max(e1Lower, e2Lower);
			}
			
		}
		Element mergeElement = new Element(mergeAttr,mergeLower,mergeUpper,mergeEqual);
		
		//remove old element and add new element
		unionFind.remove(e1);
		unionFind.remove(e2);
		unionFind.add(mergeElement);
	}
	
	/**
	 * given a union-find elements, set its lower bound, upper bound or
	 * equality constraint
	 * */
	public void updateElement(Element e, Long lower, Long upper, Long equal){
		HashSet<Column> attr = e.getattri();
		Element updateElement = new Element(attr, lower, upper, equal);
		unionFind.remove(e);
		unionFind.add(updateElement);
	}

	public ArrayList<Element> getUFlist() {
		return this.unionFind;
	}
	
	@Override
	public String toString() {
		String result = "";
		for(Element e: unionFind){
			result += e.toString();
		}
		return result;
	}
	
}
