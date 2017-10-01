package Operator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

import Database_Catalog.Catalog;
import Tuple.Tuple;
import net.sf.jsqlparser.statement.select.PlainSelect;

public class JoinOperator extends Operator {
	
	Catalog tableCatalog; //Store data schema and file location
	PlainSelect parseBody; //Store the plainSelect object parsed from query
	
	public JoinOperator(Operator left, Operator right, Catalog table, PlainSelect selectBody) {
		// TODO Auto-generated constructor stub
		tableCatalog = table;
		parseBody = selectBody;
	}
	
	@Override
	public Tuple getNextTuple() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dump() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public ArrayList<Tuple> writeToFile() {
		// TODO Auto-generated method stub
		Tuple a =new Tuple("");
		ArrayList<Tuple> result = new ArrayList<Tuple>();
		while((a=getNextTuple()) != null){
			result.add(a);
		}
		return result;
	}

}
