package physicalOperator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import Database_Catalog.Catalog;
import Interpreter.BinaryTR;
import Tuple.Tuple;

public class ScanOperatorBinary extends Operator{

	BufferedReader br;
	String input;
	String inputStar;
	//String originName;
	String location;
	File inputFile;
	BinaryTR binaryReader;
	
	public ScanOperatorBinary(String tableName) throws IOException {
		// TODO Auto-generated constructor stub
		//input = selectBody.getFromItem();
		Catalog data = Catalog.getInstance();
		HashMap<String, String> pairAlias = data.getPairAlias();
		
		location = Catalog.getInstance().getInputLocation();
		input = tableName; //Original name
		inputStar = tableName; // the name containing *, which is a second copy of table
		if (input.contains("*")) {
			input = input.substring(0, input.length()-1);
		}
		//originName = pairAlias.get(tableName);
		String filePath = location + "/db/data/" + input;
		inputFile = new File(filePath); 
		binaryReader = new BinaryTR(inputFile);
		
		}
		
	
	/** This method return the satisfied tuple and get next tuple from the child operator.
	 * @return the next tuple 
	 * */
	@Override
	public Tuple getNextTuple() {
		// TODO Auto-generated method stub
		String tupleString;
		while ((tupleString = binaryReader.ReadNextTuple()) != null) {
			//System.out.println(tupleString);
			ArrayList<String> tableList = new ArrayList<>();
			tableList.add(inputStar); // add as original table
			Tuple tuple = new Tuple(tupleString, tableList);
			return tuple;
		}

		return null;	
	}

	/**Reset the operator to re-call from the beginning */
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		try {
			binaryReader = new BinaryTR(inputFile);
		} 
		catch (IOException e) {
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
