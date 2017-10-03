package Operator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

import Database_Catalog.Catalog;
import Tuple.Tuple;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.PlainSelect;

public class ScanOperator extends Operator {
	BufferedReader br;
	String input;
	String originName;
	String location;
	
	public ScanOperator(String tableName, HashMap<String, String> pairAlias) throws IOException {
		// TODO Auto-generated constructor stub
		//input = selectBody.getFromItem();
		location = Catalog.getInstance().getInputLocation();
		input = tableName;
		originName = pairAlias.get(tableName);
		br = new BufferedReader(new FileReader(location + "/db/data/" + originName));      
		
		}
		
	

	@Override
	public Tuple getNextTuple() {
		// TODO Auto-generated method stub
		String test;
		try {
			while ((test = br.readLine()) != null) {
			       // process the line.
				ArrayList<String> l = new ArrayList<>();
				//System.out.println("hh"+input);
				l.add(input);
				//System.out.println("yoyo"+l.toString());
				Tuple tuple = new Tuple(test,l);
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
			br = new BufferedReader(new FileReader(location + "/db/data/"+originName));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}

	@Override
	public void dump() {
		// TODO Auto-generated method stub
		ArrayList l = new ArrayList();
		l.add(input);
		Tuple a =new Tuple("",l);
		while((a=getNextTuple()) != null){
			System.out.println(a.getTuple());
			//System.out.println(a.toString());
		}
		
		
	}
	
	@Override
	public ArrayList<Tuple> writeToFile() {
		// TODO Auto-generated method stub
		ArrayList l = new ArrayList();
		l.add(input);
		Tuple a =new Tuple("",l);
		ArrayList<Tuple> result = new ArrayList<Tuple>();
		while((a=getNextTuple()) != null){
			result.add(a);
		}
		return result;
	}


}
