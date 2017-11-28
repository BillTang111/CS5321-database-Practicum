package Database_Catalog;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
	private String inputLocation;
	
	public StatsInfo(){
		//build table List
		tableList = new ArrayList<String>();
		Catalog catalog = Catalog.getInstance();
		inputLocation = catalog.getInputLocation();
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
							ArrayList<Integer> bound = fieldAndBound.get(fieldNow);
							int lowerBound = bound.get(0);
							int upperBound = bound.get(1);
							if(fieldNumNow< lowerBound) lowerBound = fieldNumNow;
							if(fieldNumNow>upperBound) upperBound = fieldNumNow;
							ArrayList<Integer> newBound = new ArrayList<Integer>();
							newBound.add(lowerBound);
							newBound.add(upperBound);
							fieldAndBound.put(fieldNow, newBound);
						}else{
							//if the field does not exists before, create it
							ArrayList<Integer> newBound =  new ArrayList<Integer>();
							newBound.add(fieldNumNow);
							newBound.add(fieldNumNow);
							fieldAndBound.put(fieldNow, newBound);
						}
					}
				}// end of while loop
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//update table and size map
			tableAndSizeMap.put(table, tableSize);
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
		try {
			FileWriter fw = new FileWriter(inputLocation + "/db/stats.txt");
			BufferedWriter bw = new BufferedWriter(fw);
			for(int i=0; i<tableList.size(); i++){
				StringBuilder singleLine = new StringBuilder();
				String table = tableList.get(i);
				singleLine.append(table+" ");
				Integer tableSize = tableAndSizeMap.get(table);
				singleLine.append(tableSize+" ");
				ArrayList fields = tableAndFieldMap.get(table);
				for(int k=0; k<fields.size(); k++){
					String field = fields.get(k).toString();
					singleLine.append(field+",");
					ArrayList<Integer> bound = fieldAndBound.get(field);
					singleLine.append(bound.get(0)+","+bound.get(1)+" ");
				}
				bw.write("\n");
			}
			bw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
