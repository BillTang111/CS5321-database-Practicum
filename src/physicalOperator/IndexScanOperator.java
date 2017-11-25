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
import visitor.printLogicalQueryPlanVisitor;
import visitor.printPhysicalQueryPlanVisitor;

/** An index scan will only retrieve a range (subset) of tuples from a relation file,
 *  and will use a B+-tree index to do so.
 *  
 * @author benzhangtang
 */

public class IndexScanOperator extends Operator{
	private Long lowkey;
	private Long highkey;
	private String tableName;
	private BPlusIndexInform indexinform;
	private BPlusTreeDeserializer deserializer;
	private BinaryTR BtupleReader;
	private List<DataEntry> dataEntryList;
	private ListIterator<DataEntry> lstIterator;
	private boolean foundClusterEntry; 


	public IndexScanOperator(Long lowkey, Long highkey, String tableName, BPlusIndexInform indexinform) throws IOException {
		this.lowkey = lowkey;
		this.highkey = highkey;
		this.tableName = tableName;
		this.indexinform = indexinform;
		this.deserializer = new BPlusTreeDeserializer(indexinform);
		this.foundClusterEntry = false;


		if(!indexinform.isClustered()) {
			this.dataEntryList = deserializer.getEntries(lowkey, highkey);
			System.out.print("111111");
			this.lstIterator = this.dataEntryList.listIterator();
		}
		
		String inputloc = Catalog.getInstance().getInputLocation() + "/db/data/"+ tableName;
		System.out.print("input location is: " + inputloc + "\n");
		File inputFile = new File(inputloc);
		this.BtupleReader = new BinaryTR(inputFile);
		//System.out.println("try BtupleReader:" + BtupleReader.ReadNextTuple());
		//BtupleReader.reset();
	}


	@Override
	public Tuple getNextTuple() {
		// TODO Auto-generated method stub
		//System.out.println("sss");
		if(indexinform.isClustered()) {
			//System.out.println("haha");
			if(!foundClusterEntry) {
				//System.out.println("yoyo");
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
					System.out.println("Starting tuple:" + input);
					if(input == null) {return null;}
					List nameList = new LinkedList<String>();
					nameList.add(tableName);
					return new Tuple(input,nameList);
				}
					return null;
			}else { 
				String input = BtupleReader.ReadNextTuple();
				if (input == null)  {return null;}
				List nameList = new  LinkedList<String>();
				nameList.add(tableName);
				Tuple tuple = new Tuple(input,nameList);
				//Tuple t = new Tuple(input,nameList);
				if (input != null) {
					//System.out.println(tuple.getTuple().toString());
					//System.out.println("field: "+ indexinform.getColumn());
					int keyFieldNumber = (int) tuple.getTupleMap().get(indexinform.getColumn());
					int key =Integer.parseInt((String) tuple.getTuple().get(keyFieldNumber));
					
					if (highkey==null) {
						return tuple;
					}
					
					if (key<highkey) {
						System.out.println("key: "+key+" highkey: "+highkey);
						return tuple;
					}
				}
				return null;
			}
		} else {//data is not clustered
			//System.out.println("hh");
			if(this.lstIterator.hasNext()) {
				
				DataEntry entry = this.lstIterator.next();
				int pageId = entry.getPageId();
				int tupleId = entry.getTupleId();
				//System.out.println("tupleId: "+tupleId);
				try {
					BtupleReader.reset(pageId,tupleId);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String input = BtupleReader.ReadNextTuple();
				//System.out.println("\n"+"this is input tuple:" + input);
				//System.out.println("this is input table:" + tableName + "\n");
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


	@Override
	public void accept(
			printPhysicalQueryPlanVisitor printPhysicalQueryPlanVisitor) {
		printPhysicalQueryPlanVisitor.visit(this);	
	}

}
