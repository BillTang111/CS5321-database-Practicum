package Tuple;

import java.util.ArrayList;

import net.sf.jsqlparser.statement.select.FromItem;

public class Tuple {
	private ArrayList tuple;
	
	
	public Tuple(String input){
		tuple = new ArrayList();
		String[] s = input.split(",");
		for(String ss:s){
			tuple.add(ss);
		}
	}
		
	public ArrayList getTuple(){
		return this.tuple;
	}
	

		
	}
	
	