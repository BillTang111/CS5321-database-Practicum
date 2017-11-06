package Interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;

import Database_Catalog.Catalog;
import Tuple.Tuple;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.PlainSelect;
import parser.LogicalQueryPlan;
import parser.Parser;
import parser.queryPlan;
import physicalOperator.Operator;
import physicalOperator.ScanOperator;
import physicalOperator.SelectOperator;
import visitor.PhysicalPlanBuilder;
import BPlusTree.BPlusTree;

/**
 * This class is the highest-level class to run configuration.
 * 
 * @author Hao Rong, hr355; Lini Tan lt398
 */
public class Interpreter {

	public Interpreter() {
		// TODO Auto-generated constructor stub
	}

	/**main function to run*/
	public static void main(String[] args) throws IOException {
		// 1.1 Get input&output directory from command line argument

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	    String command = reader.readLine();
	    int start = command.indexOf(".jar")+5;


		String configLocation = command.substring(start);

		
//		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//	    String command = reader.readLine();
//	    int start = command.indexOf(".jar")+5;
//		String sub = command.substring(start);
//		int end = sub.indexOf(" ");
//		String inputLocation = sub.substring(0, end);
//		String outAndTemp = sub.substring(end+1);
//		end = outAndTemp.indexOf(" ");
//		String outputLocation = outAndTemp.substring(0, end);
//		String tempLocation = outAndTemp.substring(end+1);
//	    System.out.println("input: " + inputLocation);
//	    System.out.println("output: " + outputLocation);
//	    System.out.println("temp: " + tempLocation);
		
		

//		String sub = command.substring(start);
//		int end = sub.indexOf(" ");
//		String inputLocation = sub.substring(0, end);
//		String outAndTemp = sub.substring(end+1);
//		end = outAndTemp.indexOf(" ");
//		String outputLocation = outAndTemp.substring(0, end);
//		String tempLocation = outAndTemp.substring(end+1);
//	    System.out.println("input: " + inputLocation);
//	    System.out.println("output: " + outputLocation);
//	    System.out.println("temp: " + tempLocation);

	    
//		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//	    String command = reader.readLine();
//	    int start = command.indexOf(".jar")+5;
//		String sub = command.substring(start);
//		int end = sub.indexOf(" ");
//		String inputLocation = sub.substring(0, end);
//		String outputLocation = sub.substring(end+1);
//	    System.out.println("input: " + inputLocation);
//	    System.out.println("output: " + outputLocation);

	    
//		if (args.length<2) {
//		      System.err.println( "Usage: java FastCopyFile infile outfile" );
//		      System.exit( 1 );
//		    }
		
		
	// submission start here
//		String configLocation = args[0];
//		String inputLocation = args[0];
//		String outputLocation = args[1];
//		String tempLocation = args[2];

			BufferedReader configReader = new BufferedReader(new FileReader(configLocation));
			String inputLocation = configReader.readLine();
			String outputLocation = configReader.readLine();
			String tempLocation = configReader.readLine();
			//whether the interpreter should build indexes
			boolean buildIndex = false;
			if(Integer.parseInt(configReader.readLine())==1){
				buildIndex = true;
			}
			//whether the interpreter should actually evaluate the SQL queries
			boolean evaluateOrNot = false;
			if(Integer.parseInt(configReader.readLine())==1){
				evaluateOrNot = true;
			}
			configReader.close();
			
			
			


			
		//FileInputStream fin = new FileInputStream( inputLocation );
		
		
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
		
		// 1.3 Get the configuration file to be later set up in physicalPlanBuilder
		BufferedReader br2 = new BufferedReader(new FileReader(inputLocation+"/plan_builder_config.txt"));
		StringBuilder sb2 = new StringBuilder();
	    String joinConfigLine = br2.readLine();
	    String sortConfigLine = br2.readLine();
	    br2.close();
		
		// 1.4 Create a 'Database_Catalog' object to store directory and schema
	    //test java -jar cs4321_p3.jar /Users/tanlini/Desktop/samples/interpreter_config_file.txt
		  //test java -jar cs4321 p2.jar /Users/benzhangtang/Desktop/samples/input /Users/benzhangtang/Desktop/samples/test_output
		 //test java -jar cs4321_p2.jar /Users/benzhangtang/Desktop/samples/input /Users/benzhangtang/Desktop/samples/test_output /Users/benzhangtang/Desktop/samples/temp
		 //test java -jar cs4321_p3.jar /Users/tanlini/Desktop/samples/input /Users/tanlini/Desktop/samples/test_output /Users/tanlini/Desktop/samples/temp
		 //test java -jar cs4321_p2.jar /Users/LukerRong/Desktop/CS5321/input /Users/LukerRong/Desktop/CS5321/test_output
		 //test java -jar cs4321_p2.jar /Users/LukerRong/Desktop/project2copy/input /Users/LukerRong/Desktop/project2copy/output /Users/LukerRong/Desktop/project2copy/temp
		Catalog catalog = Catalog.getInstance();
		catalog.setinputLocation(inputLocation);
		catalog.setoutputLocation(outputLocation);
		catalog.settempLocation(tempLocation);
		catalog.setJoinConfig(joinConfigLine);
		catalog.setSortConfig(sortConfigLine);
		catalog.setSchema(map); // Original name + field
		
		// if need to build index
		if(buildIndex){
			System.out.println("need to build index");
			buildIndex(inputLocation);
		}
		
		// if we want to actually evaluate the SQL queries
		if(evaluateOrNot){
		
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
			File tempFolder = new File(catalog.getTempLocation());
			catalog.setQueryNumber(1);
			int i; //File number denoted
			
			
			for(PlainSelect eachQuerySelect: queryList){
				i = catalog.getQueryNumber();
				cleanFolder(tempFolder);
				
				//queryPlan plan = new queryPlan(eachQuerySelect);
				LogicalQueryPlan logPlan = new LogicalQueryPlan(eachQuerySelect);
				PhysicalPlanBuilder builder = new PhysicalPlanBuilder();
				logPlan.getRoot().accept(builder);
				Operator physicalPlanRoot = builder.getRoot();
				
			// Option 1: dump result and see benchmark time
//				long timeStart = System.currentTimeMillis();
//				System.out.print("Current Time in milliseconds = ");
//				System.out.println(timeStart);
//				physicalPlanRoot.dump(0); //change to 1 when need to print out result
//				System.out.println("Results dumped.");
//				long timeEnd = System.currentTimeMillis();
//				System.out.print("Current Time in milliseconds = ");
//				System.out.println(timeEnd);
//				System.out.print("Cost time = ");
//				System.out.println(timeEnd - timeStart);

			
				
				String outputPath = outputLocation + "/query" + i;
				File file = new File(outputPath);
				System.out.println(outputLocation + "/query" + i);
				if (!file.exists()) {
					file.createNewFile();
				}
				
			// Option 2: Project 2 Write to file	
				
//				FileWriter fw = new FileWriter(file);
//				BufferedWriter bw = null;
//				bw = new BufferedWriter(fw);
//				
//				for(Tuple oneLine: result){
//					String stringResult = String.join(",", oneLine.getTuple());
//					bw.write(stringResult);
//					bw.newLine();
//				}
//				
//				bw.close();
				
			// Option 3: Human TW
//				ArrayList<Tuple> result = physicalPlanRoot.getAllTuple();  //Out-of-Bond Method
//				
//				HumanTW humanWriter = new HumanTW(file);
//				
//				for(Tuple oneLine: result){
//					humanWriter.WriteTuple(oneLine);
//				}
//				
//				humanWriter.close();
			
				
			// Option 4: Binary TW
//				BinaryTW binaryWriter = new BinaryTW(outputPath);
//				Tuple resultT = physicalPlanRoot.getNextTuple();
//				while (resultT!=null) {
//					binaryWriter.WriteTuple(resultT);
//					resultT = physicalPlanRoot.getNextTuple();
//				}
//				binaryWriter.close();
				
				
				
				
//				for(Tuple oneLine: result){
//					binaryWriter.WriteTuple(oneLine);
//				}
//				
				
				System.out.println("Results wrote in file.");
				i++;
				catalog.setQueryNumber(i);
			}
		}// end of evaluation
		
			// 3.3 Close the file.		
		
		// 2.3 Repeat step 3 for the remaining query until reaching the end
	}
	
	
	/** Clean up the folder of a directory. */
	public static void cleanFolder(File folder){
		File[] files = folder.listFiles();
		if (files != null) {
			for (File f : files) {
				if (f.isDirectory()){
					cleanFolder(f);
					f.delete();
				} else {
					f.delete();
				}
			}
		}
	}
	
	/** Build index for a special column. */
	public static void buildIndex(String input) throws IOException {
		BufferedReader indexReader = new BufferedReader(new FileReader(input + "/db/index_info.txt"));
		String config;
		while((config = indexReader.readLine()) != null){
			//split each line and build corresponding b+ tree
			String[] configs = config.split("\\s+");
			String tableName = configs[0];
			String columnName = configs[1];
			boolean clusterOrNot = configs[2].equals("1");
			int order = Integer.parseInt(configs[3]);
			BPlusTree indexTree = new BPlusTree(clusterOrNot, tableName, columnName, order, input + "/db/");
		}
	}
	
	

}
