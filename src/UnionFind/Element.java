package UnionFind;

import java.util.HashSet;

/**
 * This class is the element which consists of union find
 * 
 * author: Lini Tan lt398
 * */
public class Element {
	private HashSet<String> attribute;
	private Long lowerBound;
	private Long upperBound;
	private Long equality;
	
	public Element(HashSet<String>attri, Long lower, Long upper, Long equal){
		attribute = attri;
		lowerBound = lower;
		upperBound = upper;
		equality = equal;
	}
	
	public void setLowerBound(Long value){
		lowerBound = value;
	}
	
	public void setUpperBound(Long value){
		upperBound = value;
	}
	
	public void setEquality(Long value){
		equality = value;
	}
	
	public void setAttri(HashSet<String> attr){
		attribute = attr;
	}
	
	public void addAttr(String attr){
		attribute.add(attr);
	}
	
	public Long getLowerBound(){
		return lowerBound;
	}
	
	public Long getUpperBound(){
		return upperBound;
	}
	
	public Long getEquality(){
		return equality;
	}
	
	public HashSet<String> getattri(){
		return attribute;
	}
	
	
}
