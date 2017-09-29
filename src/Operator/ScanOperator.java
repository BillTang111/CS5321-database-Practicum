package Operator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import Database_Catalog.Catalog;
import Tuple.Tuple;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.PlainSelect;

public class ScanOperator extends Operator {
	BufferedReader br;
	FromItem input;
	String location;
	
	public ScanOperator(PlainSelect selectBody) throws IOException {
		// TODO Auto-generated constructor stub
		input = selectBody.getFromItem();
		location = Catalog.getInstance().getInputLocation();	
		br = new BufferedReader(new FileReader(location + "/db/data/"+input));      
		
		}
		
	

	@Override
	public Tuple getNextTuple() {
		// TODO Auto-generated method stub
		String test;
		try {
			while ((test = br.readLine()) != null) {
			       // process the line.
				Tuple tuple = new Tuple(test);
				return tuple;
		    }
			 br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;	
	}

	
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		try {
			br = new BufferedReader(new FileReader(location + "/db/data/"+input+".txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}

	@Override
	public void dump() {
		// TODO Auto-generated method stub
		Tuple a =new Tuple("");
		while((a=getNextTuple()) != null){
			System.out.println(a.getTuple());
		}
		
		
	}
	
//	 public static void main(String args[])throws Exception{  
//		 
//		 }  


}
