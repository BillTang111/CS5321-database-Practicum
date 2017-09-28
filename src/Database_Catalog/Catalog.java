package Database_Catalog;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class Catalog {

	String inputLocation;
	String outputLocation;
	HashMap schemaMap;
	
	
	public Catalog(){
		String command = System.console().readLine();
		int start = command.indexOf(".jar")+5;
		String sub = command.substring(start);
		int end = sub.indexOf(" ");
		inputLocation = sub.substring(0, end);
		outputLocation = sub.substring(end+1);
	}
		//read file 

	//java -jar cs4321 p2.jar inputdir outputdir
	  public static void main(String[] args) throws IOException {
		    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		    String command = reader.readLine();
		    int start = command.indexOf(".jar")+5;
		    System.out.println(start);
			String sub = command.substring(start);
			System.out.println(sub);
			int end = sub.indexOf(" ");
			System.out.println(end);
			String inputLocation = sub.substring(0, end);
			String outputLocation = sub.substring(end+1);
		    System.out.println("input: " + inputLocation);
		    System.out.println("output: " + outputLocation);
		  
		//read file 
		  
//		  BufferedReader br = new BufferedReader(new FileReader("schema.txt"));
//		  HashMap<String, ArrayList> map = new HashMap<>();
//		  try {
//		      StringBuilder sb = new StringBuilder();
//		      String line = br.readLine();
//
//		      while (line != null) {
//		    	  System.out.println("new line " + line);
//		    	  String[] oneLine = line.split(" ");
//		    	  ArrayList fields = new ArrayList();
//		    	  for(int i=1; i<oneLine.length; i++){
//		    		  fields.add(oneLine[i]);
//		    	  }
//		    	  map.put(oneLine[0], fields);
//		    	  
//		          sb.append(line);
//		          sb.append(System.lineSeparator());
//		          line = br.readLine();
//		      }
//		      String everything = sb.toString();
//		      System.out.println(everything);
//		  } finally {
//		      br.close();
//		      
//		  }
			
		  
}
}