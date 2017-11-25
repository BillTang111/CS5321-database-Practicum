package logicalOperator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import Database_Catalog.Catalog;
import Tuple.Tuple;
import net.sf.jsqlparser.statement.select.PlainSelect;
import visitor.PhysicalPlanBuilder;
import visitor.printLogicalQueryPlanVisitor;

/**
 * This class is the abstract operator class
 * 
 * @author Hao Rong, hr335
 */
public abstract class LogicalOperator implements Iterator {


	/**@return a boolean whether it has next */
	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object next() {
		// TODO Auto-generated method stub
		return null;
	}

	public abstract void accept(printLogicalQueryPlanVisitor lpv) throws IOException;
	
	public abstract void accept(PhysicalPlanBuilder pplanbuilder) throws IOException;
}
