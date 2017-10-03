package Operator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Database_Catalog.Catalog;
import Tuple.Tuple;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;

public class ProjectOperator extends Operator {
	HashMap map;
	List project;
	Operator childOp;
	String table;
	HashMap<String, String> pairAlias;
	
	public ProjectOperator(PlainSelect selectBody, Operator op) {
		// TODO Auto-generated constructor stub
		childOp = op;
		project = selectBody.getSelectItems();
		Catalog c = Catalog.getInstance();
		map=c.getSchema();
		table ="";
		
		pairAlias = c.getPairAlias();
	}

	@Override
	public Tuple getNextTuple() {
		// TODO Auto-generated method stub
		Tuple a = childOp.getNextTuple();
		while(a!=null){
		ArrayList aList = a.getTuple();
		AllColumns allColumns = new AllColumns();
		if(project.get(0).getClass()==allColumns.getClass()) return a;
		int length = project.size();
		//build new Tuple
		String rowRecord = "";
		for(int i =0; i<length; i++){
			SelectExpressionItem sei = (SelectExpressionItem) project.get(i);
			String e = sei.getExpression().toString();
			//System.out.println("sei: " + e);
			
			
			int dotIndex = e.indexOf(".");
			table = pairAlias.get(e.substring(0, dotIndex)); // original table name
			//System.out.println(pairAlias.toString());
			String newColumnField = table + "." + e.substring(dotIndex+1 , e.length());
			
			//System.out.println("newColumnField: " + newColumnField);
			
			int index = (int) a.getTupleMap().get(newColumnField);
			//System.out.println(index);

			String cellRecord = (String) aList.get(index);
			rowRecord = rowRecord + cellRecord +",";
			//System.out.println(s);
		}
		rowRecord = rowRecord.substring(0, rowRecord.length()-1);
		ArrayList l = new ArrayList();
		l.add(table);
		Tuple b = new Tuple(rowRecord,l);
		
		return b;
		}
		return null;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		childOp.reset();
	}

	@Override
	public void dump() {
		// TODO Auto-generated method stub
		Tuple a=getNextTuple();
		while(a != null){
			System.out.println(a.getTuple());
			a=getNextTuple();
		}
	}
	
	@Override
	public ArrayList<Tuple> writeToFile() {
		// TODO Auto-generated method stub
		Tuple a=getNextTuple();
		ArrayList<Tuple> result = new ArrayList<Tuple>();
		while(a != null){
			result.add(a);
			a=getNextTuple();
		}
		return result;
	}

}
