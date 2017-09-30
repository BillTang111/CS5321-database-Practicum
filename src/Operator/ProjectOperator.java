package Operator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Database_Catalog.Catalog;
import Tuple.Tuple;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.PlainSelect;

public class ProjectOperator extends Operator {
	HashMap map;
	List project;
	Operator previousOp;
	
	public ProjectOperator(PlainSelect selectBody, Operator op) {
		// TODO Auto-generated constructor stub
		previousOp = op;
		project = selectBody.getSelectItems();
		Catalog c = Catalog.getInstance();
		map=c.getSchema();
	}

	@Override
	public Tuple getNextTuple() {
		// TODO Auto-generated method stub
		Tuple a = previousOp.getNextTuple();
		ArrayList aList = a.getTuple();
		AllColumns allColumns = new AllColumns();
		if(project.get(0).getClass()==allColumns.getClass()) return a;
		int length = project.size();
		//build new Tuple
		String s = "";
		for(int i =0; i<length; i++){
			Column c = (Column)project.get(i);
			String t = c.getTable().getName();
			ArrayList field = (ArrayList) map.get(t);
			String cn = c.getColumnName().toString();
			int index = field.indexOf(cn);
			String ss = (String) aList.get(index);
			s = s+ss+",";
		}
		s = s.substring(0, s.length()-1);
		Tuple b = new Tuple(s);
		return b;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		previousOp.reset();
	}

	@Override
	public void dump() {
		// TODO Auto-generated method stub
		
	}

}
