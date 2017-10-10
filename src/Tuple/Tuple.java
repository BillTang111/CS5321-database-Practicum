package Tuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Database_Catalog.Catalog;
import net.sf.jsqlparser.statement.select.FromItem;

/**
 * This class is used to initialize the tuple.
 * 
 * @author Lini Tan, lt398; Hao Rong, hr355
 */
public class Tuple {

	private ArrayList tuple;
	private HashMap tupleMap;
	private List nameList; //Original name
	
	public Tuple(String input, List<String> tableName){
		nameList = tableName;
		Catalog data = Catalog.getInstance();
		ArrayList mapKey = new ArrayList();
		
		tuple = new ArrayList();
		tupleMap = new HashMap<String, Integer>();
		
		for(int i=0; i<tableName.size(); i++){
			ArrayList a = (ArrayList) data.getSchema().get(tableName.get(i));
			for (int j=0; j<a.size();j++){
				mapKey.add(tableName.get(i)+"."+a.get(j));
			}
			
		}
		
		String[] s = input.split(",");
		for(int i=0; i<s.length; i++){
			//System.out.println(s[i]);
			tuple.add(s[i]);
			//System.out.println(tupleMap);
			tupleMap.put(mapKey.get(i), i);
			
		}
	}
	
	/** This method return tuple list
	 * @return an array list of tuple
	 * */
	public ArrayList getTuple(){
		return this.tuple;
	}
	
	/** This method return hash map which record the relation between tuple and schema
	 * @return a hash map of the relationship between tuple and schema
	 * */
	public HashMap getTupleMap(){
		return this.tupleMap;
	}
	
	/** This method return hash map which record the relation between tuple and schema
	 * @param a hash map of the relationship between tuple and schema
	 * */
	public void setTupleMap(HashMap newmap){
		this.tupleMap = newmap;
	}
	
	/** This method return tuple list
	 * @return a table name list where the tuple comes from 
	 * */
	public List getNameList(){
		return this.nameList;
	}

		
	}
	
	