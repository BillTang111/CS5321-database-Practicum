package physicalOperator;

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
import visitor.printLogicalQueryPlanVisitor;
import visitor.printPhysicalQueryPlanVisitor;

/**
 * This class is used when sql query contains select condition.
 * 
 * @author Lini Tan, lt398
 */
public class ProjectOperator extends Operator {
	HashMap map;
	List project;
	Operator childOp;
	String table;
	HashMap<String, String> pairAlias;
	
	public ProjectOperator(PlainSelect selectBody, Operator op) {
		// TODO Auto-generated constructor stub
		childOp = op;
		project = selectBody.getSelectItems();
		Catalog c = Catalog.getInstance();
		map=c.getSchema();
		table ="";
		
		pairAlias = c.getPairAlias();
	}

	/** This method return the satisfied tuple and get next tuple from the child operator.
	 * @return the next tuple 
	 * */
	@Override
	public Tuple getNextTuple() {
		// TODO Auto-generated method stub
		Tuple a = childOp.getNextTuple();
		while(a!=null){
		ArrayList aList = a.getTuple();
		AllColumns allColumns = new AllColumns();
		if(project.get(0).getClass().equals(allColumns.getClass())) return a;
		int length = project.size();
		
		//fix tuplemap
		ArrayList newName = new ArrayList();
		for(int i =0; i<length; i++){
			SelectExpressionItem sei = (SelectExpressionItem) project.get(i);
			String e = sei.getExpression().toString();
			int dotIndex = e.indexOf(".");
			table = pairAlias.get(e.substring(0, dotIndex)); // original table name
			//System.out.println(pairAlias.toString());
			String newColumnField = table + "." + e.substring(dotIndex+1 , e.length());
			newName.add(newColumnField);
		}
		
		//build new Tuple
		String rowRecord = "";
		for(int i =0; i<length; i++){
			SelectExpressionItem sei = (SelectExpressionItem) project.get(i);
			String e = sei.getExpression().toString();
			
			
			int dotIndex = e.indexOf(".");
			table = pairAlias.get(e.substring(0, dotIndex)); // original table name
			//System.out.println(pairAlias.toString());
			String newColumnField = table + "." + e.substring(dotIndex+1 , e.length());
			
			//System.out.println("newColumnField: " + newColumnField);
			
			int index = (int) a.getTupleMap().get(newColumnField);
			//System.out.println(index);

			String cellRecord = (String) aList.get(index);
			rowRecord = rowRecord + cellRecord +",";
			//System.out.println(s);
		}
		rowRecord = rowRecord.substring(0, rowRecord.length()-1);
		ArrayList l = new ArrayList();
		l.add(table);
		Tuple b = new Tuple(rowRecord,l);
		HashMap newmap = setNewTupleMap(b,newName);
		b.setTupleMap(newmap);
		//System.out.println("tuple: "+b.getTuple().toString());
		return b;
		}
		return null;
	}

	public HashMap setNewTupleMap(Tuple b, ArrayList newName){
		HashMap newmap = new HashMap();
		//System.out.println(newName.toString());
		for(int i=0; i<newName.size(); i++){
			newmap.put(newName.get(i).toString(), i);
		}
		return newmap;	
	}
	
	
	/**Reset the operator to re-call from the beginning */
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		childOp.reset();
	}

	/**To print your result. Use for debug 
	 * @param printOrNot: 0: don't print, 1: print*/
	@Override
	public void dump(int printOrNot) {
		// TODO Auto-generated method stub
		Tuple a =getNextTuple();
		if (printOrNot==1){
			while(a != null){
				System.out.println(a.getTuple());
				a=getNextTuple();
				//System.out.println(a.toString());
			}
		} 
		else if (printOrNot==0){
			while(a != null){
				a=getNextTuple();
			}
		}
	}
	
	/** Get all the result tuple in this operator (For debugging) 
	 * @return a list of tuple
	 */
	@Override
	public ArrayList<Tuple> getAllTuple() {
		// TODO Auto-generated method stub
		Tuple a=getNextTuple();
		ArrayList<Tuple> result = new ArrayList<Tuple>();
		while(a != null){
			result.add(a);
			a=getNextTuple();
		}
		return result;
	}

	@Override
	public void reset(int index) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getIndex() {
		// TODO Auto-generated method stub
		return 0;
	}


	public Operator getChild(){
		return this.childOp;
	}

	@Override
	public void accept(
			printPhysicalQueryPlanVisitor printPhysicalQueryPlanVisitor) {
		printPhysicalQueryPlanVisitor.visit(this);
	}

	public String getProjectField() {
		return project.toString();
	}
}
