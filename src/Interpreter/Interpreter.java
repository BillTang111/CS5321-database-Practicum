package Interpreter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import Database_Catalog.Catalog;
import Operator.Operator;
import Operator.ScanOperator;
import Operator.SelectOperator;
import Tuple.Tuple;
import net.sf.jsqlparser.statement.select.PlainSelect;
import parser.Parser;
import parser.queryPlan;

public class Interpreter {

	public Interpreter() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws IOException {
		// 1.1 Get input&output directory from command line argument
//		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//	    String command = reader.readLine();
//	    int start = command.indexOf(".jar")+5;
//		String sub = command.substring(start);
//		int end = sub.indexOf(" ");
//		String inputLocation = sub.substring(0, end);
//		String outputLocation = sub.substring(end+1);
//	    System.out.println("input: " + inputLocation);
//	    System.out.println("output: " + outputLocation);
		String inputLocation = args[0];
		String outputLocation = args[1];
		
		// 1.2 Get the name of the schema file from command line argument,
		// read the schema file and to store it a Hashmap.
	    BufferedReader br = new BufferedReader(new FileReader(inputLocation+"/db/schema.txt"));
		 HashMap<String, ArrayList> map = new HashMap<String, ArrayList>();
		  try {
		      StringBuilder sb = new StringBuilder();
		      String line = br.readLine();

		      while (line != null) {
		    	  System.out.println("new line " + line);
		    	  String[] oneLine = line.split(" ");
		    	  ArrayList fields = new ArrayList();
		    	  for(int i=1; i<oneLine.length; i++){
		    		  fields.add(oneLine[i]);
		    	  }
		    	  map.put(oneLine[0], fields);
		    	  
		          sb.append(line);
		          sb.append(System.lineSeparator());
		          line = br.readLine();
		      }
		      String everything = sb.toString();
		      System.out.println(everything);
		  } finally {
		      br.close();
		      
		  }
		
		// 1.3 Create a 'Database_Catalog' object to store directory and schema
		 //test java -jar cs4321 p2.jar /Users/tanlini/Desktop/samples/input /Users/tanlini/Desktop/samples/test_output
		 //test java -jar cs4321_p2.jar /Users/LukerRong/Desktop/CS5321/input /Users/LukerRong/Desktop/CS5321/test_output
		Catalog catalog = Catalog.getInstance();
		catalog.setinputLocation(inputLocation);
		catalog.setoutputLocation(outputLocation);
		catalog.setSchema(map); // Original name + field
		
		
		// 2.1 Get query file directory from command line argument,
		// store the content of the file into a String object
		String querypath = inputLocation + "/queries.sql";
		
		// 2.2 Use 'queryParser' to parse the query string into a collection of 
		// query plan trees composed of operators, and store each tree in a 
		// 'queryPlan' object Each operator has a field to store the 
		// corresponding 'PlainSelct' object
		Parser p = new Parser(querypath);
		ArrayList<PlainSelect> queryList = p.getQueryList();
		
			// 3.1 Create a file in the output directory. Open the file.
			
			// 3.2 Use a loop to call the getNextTuple function of the root 
			// operator, write each output in the file until reaching to the very 
			// last tuple
			int i = 1; //File number
			
			for(PlainSelect eachQuerySelect: queryList){
				queryPlan plan = new queryPlan(eachQuerySelect);
//				plan.getRoot().dump();
//				System.out.println("Results dumped.");
//				
				
				ArrayList<Tuple> result = plan.getRoot().writeToFile();
				
				File file = new File(outputLocation + "/query" + i);
				System.out.println(outputLocation + "/query" + i);
				if (!file.exists()) {
					file.createNewFile();
				}
				FileWriter fw = new FileWriter(file);
				BufferedWriter bw = null;
				bw = new BufferedWriter(fw);
				
				for(Tuple oneLine: result){
					String stringResult = String.join(",", oneLine.getTuple());
					bw.write(stringResult);
					bw.newLine();
				}
				
				bw.close();
				System.out.println("Results wrote in file.");
				i++;
			}
			
			
			
			// 3.3 Close the file.
		
		
		// 2.3 Repeat step 3 for the remaining query until reaching the end
	}
	
	

}
