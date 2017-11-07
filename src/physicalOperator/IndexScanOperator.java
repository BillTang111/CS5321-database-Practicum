package physicalOperator;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import BPlusTree.DataEntry;
import Database_Catalog.BPlusIndexInform;
import Interpreter.BPlusTreeDeserializer;
import Interpreter.BinaryTR;
import Tuple.Tuple;

/** An index scan will only retrieve a range (subset) of tuples from a relation file,
 *  and will use a B+-tree index to do so.
 *  
 * @author benzhangtang
 */

public class IndexScanOperator extends Operator{
	private Long lowkey;
	private Long highkey;
	private String tableName;
	private String alias;
	private BPlusIndexInform indexinform;
	private BPlusTreeDeserializer deserializer;
	private BinaryTR BtupleReader;
	private List<DataEntry> dataEntryList;
	private ListIterator<DataEntry> lstIterator;
	private boolean foundClusterEntry; 
	
	
	public IndexScanOperator() {
		
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
	public void dump(int printOrNot) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayList<Tuple> getAllTuple() {
		// TODO Auto-generated method stub
		return null;
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

}
