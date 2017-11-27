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
	
	
	
}
