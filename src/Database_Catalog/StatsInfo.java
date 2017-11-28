package Database_Catalog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import Interpreter.BinaryTR;

public class StatsInfo {
	//store table
	//store table and field relation
	//store table and size relation
	//store field and their bound relation
	//write to file
	private List<String> tableList;
	private HashMap<String,ArrayList> tableAndFieldMap;
	private HashMap<String,Integer> tableAndSizeMap;
	private HashMap<String,ArrayList> fieldAndBound;
	
	public StatsInfo(){
		//build table List
		tableList = new ArrayList<String>();
		Catalog catalog = Catalog.getInstance();
		String inputLocation = catalog.getInputLocation();
		HashMap schemaMap = catalog.getSchema();
		Set tableSet = schemaMap.keySet();
		for(Object table: tableSet){
			tableList.add(table.toString());
		}
		// build table and field relation map
		tableAndFieldMap = schemaMap;
		
		// initialize table and size relation map and field and Bound relation map
		tableAndSizeMap = new HashMap<String,Integer>();
		fieldAndBound = new HashMap<String,ArrayList>();
		
		//loop over every table and every field
		for(int i=0; i<tableList.size(); i++){
			String table = tableList.get(i);
			ArrayList fields = tableAndFieldMap.get(table);
			int tableSize = 0;
			String tablePath = inputLocation + "/db/data/"+table;
			File tableFile = new File(tablePath);
			try {
				BinaryTR btr = new BinaryTR(tableFile);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	
	public void writeStatsFile(){
		
	}
}
