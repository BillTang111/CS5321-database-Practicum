package Interpreter;

public class Interpreter {

	public Interpreter() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// 1.1 Get input&output directory from command line argument
		
		// 1.2 Get the name of the schema file from command line argument,
		// read the schema file and to store it a Hashmap.
		
		// 1.3 Create a 'Database_Catalog' object to store directory and schema
		
		
		
		// 2.1 Get query file directory from command line argument,
		// store the content of the file into a String object
		
		// 2.2 Use 'queryParser' to parse the query string into a query plan tree
		// composed of operators, and store the tree in a 'queryPlan' object 
		// Each operator has a field to store the corresponding 'PlainSelct' object
		
		
		
		// 3.1 Create a file in the output directory. Open the file.
		
		// 3.2 Use a while loop to call the getNextTuple function of the root 
		// operator, write each output in the file until reaching to the very 
		// last tuple
		
		// 3.3 Close the file.
	}

}
