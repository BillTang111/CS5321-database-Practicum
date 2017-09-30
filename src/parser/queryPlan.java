package parser;

import java.io.IOException;

import Operator.Operator;
import Operator.ScanOperator;
import Operator.SelectOperator;
import net.sf.jsqlparser.statement.select.PlainSelect;

public class queryPlan {
	// parse the SQL into a tree structured query plan
	private Operator root;
	
	
	public queryPlan(PlainSelect selectBody) throws IOException {
		// TODO Auto-generated constructor stub
		ScanOperator scan =  new ScanOperator(selectBody);
		root = scan;
		if (selectBody.getWhere()!=null) {
			SelectOperator select = new SelectOperator(selectBody, root);
			root = select;
		}
	}
	
	public Operator getRoot() {
		return root;
		
	}

}
