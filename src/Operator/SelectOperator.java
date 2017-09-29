package Operator;

import Database_Catalog.Catalog;
import Tuple.Tuple;
import net.sf.jsqlparser.statement.select.PlainSelect;

public class SelectOperator extends Operator {
	
	Catalog tableCatalog; //Store data schema and file location
	PlainSelect parseBody; //Store the plainSelect object parsed from query
	
	public SelectOperator() {
		// TODO Auto-generated constructor stub
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



}
