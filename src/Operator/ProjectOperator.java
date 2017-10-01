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
	
	public ProjectOperator(PlainSelect selectBody, Operator op) {
		// TODO Auto-generated constructor stub
		childOp = op;
		project = selectBody.getSelectItems();
		Catalog c = Catalog.getInstance();
		map=c.getSchema();
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
		String s = "";
		for(int i =0; i<length; i++){
			SelectExpressionItem sei = (SelectExpressionItem) project.get(i);
			String e = sei.getExpression().toString();
			int point  = e.indexOf(".");
			String t = e.substring(0, point);
			ArrayList field = (ArrayList) map.get(t);
			String cn = e.substring(point+1);
			//String cn = c.getColumnName().toString();
			int index = field.indexOf(cn);
			String ss = (String) aList.get(index);
			s = s+ss+",";
		}
		s = s.substring(0, s.length()-1);
		Tuple b = new Tuple(s);
		
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
		Tuple a =new Tuple("");
		while((a=getNextTuple()) != null){
			System.out.println(a.getTuple());
		}
	}
	
	@Override
	public void writeToFile(BufferedWriter bw) throws IOException {
		// TODO Auto-generated method stub
		Tuple a =new Tuple("");
		while((a=getNextTuple()) != null){
			String oneLineResult = String.join(",", a.getTuple());
//			bw.write(oneLineResult);
//			bw.newLine();
		}
	}

}
