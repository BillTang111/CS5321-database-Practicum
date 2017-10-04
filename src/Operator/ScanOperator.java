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

/**
 * This class is used to scan table files to become tuple.
 * 
 * @author Lini Tan, lt398
 */
public class ScanOperator extends Operator {
	BufferedReader br;
	String input;
	String inputStar;
	//String originName;
	String location;
	
	
	public ScanOperator(String tableName) throws IOException {
		// TODO Auto-generated constructor stub
		//input = selectBody.getFromItem();
		Catalog data = Catalog.getInstance();
		HashMap<String, String> pairAlias = data.getPairAlias();
		
		location = Catalog.getInstance().getInputLocation();
		input = tableName; //Original name
		inputStar = tableName;
		if (input.contains("*")) {
			input = input.substring(0, input.length()-1);
		}
		//originName = pairAlias.get(tableName);
		br = new BufferedReader(new FileReader(location + "/db/data/" + input));      
		
		}
		
	
	/** This method return the satisfied tuple and get next tuple from the child operator.
	 * @return the next tuple 
	 * */
	@Override
	public Tuple getNextTuple() {
		// TODO Auto-generated method stub
		String test;
		try {
			while ((test = br.readLine()) != null) {
			       // process the line.
				ArrayList<String> l = new ArrayList<>();
				//System.out.println("hh"+input);
				l.add(inputStar);
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

	/**Reset the operator to re-call from the beginning */
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		try {
			br = new BufferedReader(new FileReader(location + "/db/data/"+input));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}

	/**To print your result. Use for debug */
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
	
	/**Write the tuple to the file
	 * @return a list of tuple
	 */
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
