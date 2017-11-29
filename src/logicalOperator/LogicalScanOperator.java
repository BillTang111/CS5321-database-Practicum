package logicalOperator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

import Database_Catalog.Catalog;
import Tuple.Tuple;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.PlainSelect;
import visitor.PhysicalPlanBuilder;
import visitor.printLogicalQueryPlanVisitor;

/**
 * This class is used to scan table files to become tuple.
 * 
 * @author Lini Tan, lt398
 */
public class LogicalScanOperator extends LogicalOperator {
//	BufferedReader br;
	String input;
	String inputStar;
	//String originName;
//	String location;
	
	
	public LogicalScanOperator(String tableName) throws IOException {
		// TODO Auto-generated constructor stub
		//input = selectBody.getFromItem();
//		Catalog data = Catalog.getInstance();
//		HashMap<String, String> pairAlias = data.getPairAlias();
//		
//		location = Catalog.getInstance().getInputLocation();
		input = tableName; //Original name
		inputStar = tableName;
//		if (input.contains("*")) {
//			input = input.substring(0, input.length()-1);
//		}
//		//originName = pairAlias.get(tableName);
//		br = new BufferedReader(new FileReader(location + "/db/data/" + input));      
		
		}
	
	
	public String getTableName(){
		return this.input;
	}

	@Override
	public void accept(PhysicalPlanBuilder physicalPlanBuilder) throws IOException {
		physicalPlanBuilder.visit(this);
	}

	@Override
	public void accept(printLogicalQueryPlanVisitor lpv) throws IOException {
		lpv.visit(this);
	}


}
