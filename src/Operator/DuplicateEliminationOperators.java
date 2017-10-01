package Operator;

import java.io.BufferedWriter;
import java.io.IOException;

import Database_Catalog.Catalog;
import Tuple.Tuple;
import net.sf.jsqlparser.statement.select.PlainSelect;

public class DuplicateEliminationOperators extends Operator {
	
	Catalog tableCatalog; //Store data schema and file location
	PlainSelect parseBody; //Store the plainSelect object parsed from query
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
	public void writeToFile(BufferedWriter bw) throws IOException {
		// TODO Auto-generated method stub
		Tuple a =new Tuple("");
		while((a=getNextTuple()) != null){
			String oneLineResult = String.join(",", a.getTuple());
			bw.write(oneLineResult);
			bw.newLine();
		}
	}
}
