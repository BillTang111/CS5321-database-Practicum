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
	private HashMap<String,ArrayList<Integer>> fieldAndBound;
	
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
		fieldAndBound = new HashMap<String,ArrayList<Integer>>();
		
		//loop over every table and every field
		for(int i=0; i<tableList.size(); i++){
			String table = tableList.get(i);
			ArrayList fields = tableAndFieldMap.get(table);
			int tableSize = 0;
			String tablePath = inputLocation + "/db/data/"+table;
			File tableFile = new File(tablePath);
			try {
				BinaryTR btr = new BinaryTR(tableFile);
				String t;
				while ((t = btr.ReadNextTuple()) != null) {
					tableSize++;
					String[] tupleField = t.split(",");
					for(int k =0; k<tupleField.length; k++){
						//update field and bound relation map
						String fieldNow = fields.get(k).toString();
						int fieldNumNow = Integer.parseInt(tupleField[k]);
						if(fieldAndBound.containsKey(fieldNow)){
							
						}else{
							
						}
					}
				}// end of while loop
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//update table and size map
			
		}// end of for loop
		
		
	}
	
	public List<String> getTableList(){
		return tableList;
	}
	
	public HashMap<String,ArrayList> getTableAndFieldMap(){
		return tableAndFieldMap;
	}
	
	public HashMap<String,Integer> getTableAndSizeMap(){
		return tableAndSizeMap;
	} 
	
	public HashMap<String,ArrayList<Integer>> getFieldAndBound(){
		return fieldAndBound;
	}
	
	
	public void writeStatsFile(){
		
	}
}
