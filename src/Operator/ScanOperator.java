package Operator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import net.sf.jsqlparser.statement.select.FromItem;

public class ScanOperator extends Operator {

	public ScanOperator(FromItem input) {
		// TODO Auto-generated constructor stub
		BufferedReader brTest = null;
		try {
			brTest = new BufferedReader(new FileReader("/Users/tanlini/Downloads/samples 2/input/db/data/"+input+".txt"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	


}
