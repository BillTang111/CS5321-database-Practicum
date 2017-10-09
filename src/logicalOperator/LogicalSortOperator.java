package logicalOperator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Collections;


import Database_Catalog.Catalog;
import Tuple.Tuple;
import net.sf.jsqlparser.statement.select.PlainSelect;

/**
 * This class is used when sql query contains order by condition.
 * 
 * @author Lini Tan, lt398
 */
public class LogicalSortOperator extends LogicalOperator {
	
	LogicalOperator childOp;
	LinkedList<Tuple> sorted;
	List order;
	
	
	public LogicalSortOperator(LogicalOperator op, PlainSelect selectBody) {
		// TODO Auto-generated constructor stub
		childOp = op;
		sorted = new LinkedList<Tuple>();
		order = selectBody.getOrderByElements();

		
	}
	
	
}