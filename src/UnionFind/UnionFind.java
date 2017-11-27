package UnionFind;

import java.util.ArrayList;
import java.util.HashSet;

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
	public Element FindElement(String attr){
		//loop the list of union find to find the element contains attr
		
		for(Element e: unionFind){
			if(e.getattri().contains(attr)){
				return e;
			}
		}
		HashSet<String> attrList = new HashSet<String>();
		return new Element(attrList, null, null, null);
	}
	
	/**
	 * Given two union-find elements. modify the union-find data structure
	 * so that these two elements get unioned.
	 * */
	public void merge(Element e1, Element e2){
		//build new element
			//build new attrList
		HashSet<String> e1Attr = e1.getattri();
		HashSet<String> e2Attr = e2.getattri();
		HashSet<String> mergeAttr = new HashSet<String>();
		
		for(String s: e1Attr){
			mergeAttr.add(s);
		}
		
		for(String s: e2Attr){
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
			mergeUpper = Math.min(e1Upper, e2Upper);
			Long e1Lower = e1.getLowerBound();
			Long e2Lower = e2.getLowerBound();
			mergeLower = Math.max(e1Lower, e2Lower);
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
		HashSet<String> attr = e.getattri();
		Element updateElement = new Element(attr, lower, upper, equal);
		unionFind.remove(e);
		unionFind.add(updateElement);
	}
}
