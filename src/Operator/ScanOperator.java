package Operator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import Database_Catalog.Catalog;
import Tuple.Tuple;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.PlainSelect;

public class ScanOperator extends Operator {
	
	Catalog tableCatalog; //Store data schema and file location
	PlainSelect parseBody; //Store the plainSelect object parsed from query
	
	public ScanOperator(Catalog table, PlainSelect selectBody) {
		// TODO Auto-generated constructor stub
		tableCatalog = table;
		parseBody = selectBody;
		
		FromItem input = parseBody.getFromItem();
		
		BufferedReader brTest = null;
		try {
			brTest = new BufferedReader(new FileReader("/Users/tanlini/Downloads/samples 2/input/db/data/"+input+".txt"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public Tuple getNextTuple(Operator inputOperator) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
	


}
