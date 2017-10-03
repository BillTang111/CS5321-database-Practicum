package Tuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Database_Catalog.Catalog;
import net.sf.jsqlparser.statement.select.FromItem;

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
			//System.out.println(mapKey.get(i));
			tupleMap.put(mapKey.get(i), i);
			
		}
	}
		
	public ArrayList getTuple(){
		return this.tuple;
	}
	
	public HashMap getTupleMap(){
		return this.tupleMap;
	}
	
	public List getNameList(){
		return this.nameList;
	}

		
	}
	
	