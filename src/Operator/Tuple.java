package Operator;

import java.util.ArrayList;

public class Tuple {
	private ArrayList tuple;
	
	public Tuple(String input){
		tuple = new ArrayList();
		String[] s = input.split(",");
		for(String ss:s){
			tuple.add(ss);
		}
		
	}
	
	
}
