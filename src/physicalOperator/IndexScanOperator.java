package physicalOperator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import BPlusTree.DataEntry;
import Database_Catalog.BPlusIndexInform;
import Database_Catalog.Catalog;
import Interpreter.BPlusTreeDeserializer;
import Interpreter.BinaryTR;
import Tuple.Tuple;
//import net.sf.jsqlparser.schema.Column;
//import net.sf.jsqlparser.schema.Table;

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
	
	
	public IndexScanOperator(Long lowkey, Long highkey, String tableName, String alias, BPlusIndexInform indexinform) throws IOException {
		this.lowkey = lowkey;
		this.highkey = highkey;
		this.tableName = tableName;
		this.alias = alias;
		this.indexinform = indexinform;
		try {
			this.deserializer = new BPlusTreeDeserializer(indexinform);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.foundClusterEntry = false;
//		if(alias != null) {
//			HashMap catlog = Catalog.getInstance().getSchema();
//			List<Column> columnList = (List<Column>) catlog.get(tableName);
//			List<Column> newColumnList = new ArrayList<Column>();
////			?????
//			Table newTable = new Table();
//			newTable.setAlias(alias);
//			for (Column c:columnList) {
//				Column newColumn = new Column();
//				newColumn.setTable(newTable);
//				newColumn.setColumnName(c.getColumnName());
//				newColumnList.add(newColumn);
//			}
//			// ?????
//		}
		if(!indexinform.isClustered()) {
			this.dataEntryList = deserializer.getEntries(lowkey, highkey);
			this.lstIterator = this.dataEntryList.listIterator();
		}
		String inputloc = Catalog.getInstance().getInputLocation() + "";
		File inputFile = new File(inputloc);
				
		this.BtupleReader = new BinaryTR(inputFile);
	}
	

	@Override
	public Tuple getNextTuple() {
		// TODO Auto-generated method stub
		if(indexinform.isClustered()) {
			if(!foundClusterEntry) {
				DataEntry startEntry = deserializer.getLeftMostEntry(lowkey, highkey);
				if (startEntry!=null) {
					foundClusterEntry=true;
					 try {
						BtupleReader.reset(startEntry.getPageId(), startEntry.getTupleId());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					 String input = BtupleReader.ReadNextTuple();
					 List nameList = new LinkedList<String>();
					 nameList.add(tableName);
					 return new Tuple(input,nameList);
				}else {
					return null;
				}
			}else { 
				 String input = BtupleReader.ReadNextTuple();
				 List nameList = new  LinkedList<String>();
				 nameList.add(tableName);
				 Tuple t = new Tuple(input,nameList);
			if (t != null) {
				int key =(int) t.getTupleMap().get(indexinform.getColumn());
				if (key<highkey) {
					return t;
				}
			}
			return null;
				}
			} else {
					if(this.lstIterator.hasNext()) {
					DataEntry entry = this.lstIterator.next();
					int pageId = entry.getPageId();
					int tupleId = entry.getTupleId();
					try {
						BtupleReader.reset(pageId,tupleId);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					 String input = BtupleReader.ReadNextTuple();
					 List nameList = new  LinkedList<String>();
					 nameList.add(tableName);
					 return new Tuple(input,nameList);
				} 
			 else {
				 return null;
			 }
			}
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		BtupleReader.reset();
		if(!indexinform.isClustered()) {
			this.lstIterator=this.dataEntryList.listIterator();
		}
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
	
	public Long getLowKey(){
		return this.lowkey;
	}
	
	public Long getHighKey(){
		return this.highkey;
	}

}
