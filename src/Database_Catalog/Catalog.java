package Database_Catalog;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.Serializable;

public class Catalog implements Cloneable, Serializable {

	private static Catalog instance;
	
	private String inputLocation;
	private String outputLocation;
	private HashMap map;

	/* Private Constructor prevents any other class from instantiating */
	private Catalog() {
	}

	public static synchronized Catalog getInstance() {

		/* Lazy initialization, creating object on first use */
		if (instance == null) {
			synchronized (Catalog.class) {
				if (instance == null) {
					instance = new Catalog();
				}
			}
		}

		return instance;
	}

	/* Restrict cloning of object */
	@Override
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	public void display() {
		System.out.println("Hurray! I am display from Singleton!");
	}
	
	public String getInputLocation() {
		return inputLocation;
	}
	
	public String getOutputLocation() {
		return outputLocation;
	}
	
	public HashMap getSchema() {
		return map;
	}
	
	public void setinputLocation(String input) {
		this.inputLocation = input;
	}
	
	public void setoutputLocation(String output) {
		this.outputLocation = output;
	}
	
	public void setoutputLocation(HashMap schema) {
		this.map = schema;
	}
	
	
}
//		String command = System.console().readLine();
//		int start = command.indexOf(".jar")+5;
//		String sub = command.substring(start);
//		int end = sub.indexOf(" ");
//		inputLocation = sub.substring(0, end);
//		outputLocation = sub.substring(end+1);
//	}
//		//read file 
//
//	//java -jar cs4321 p2.jar inputdir outputdir
//	  public static void main(String[] args) throws IOException {
//		    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//		    String command = reader.readLine();
//		    int start = command.indexOf(".jar")+5;
//			String sub = command.substring(start);
//			int end = sub.indexOf(" ");
//			String inputLocation = sub.substring(0, end);
//			String outputLocation = sub.substring(end+1);
//		    System.out.println("input: " + inputLocation);
//		    System.out.println("output: " + outputLocation);
//		  
//		//read file 
//		  
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
//			
//		  
//}
