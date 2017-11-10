package physicalOperator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.UUID;

import Database_Catalog.Catalog;
import Interpreter.BinaryTR;
import Interpreter.BinaryTW;
import Interpreter.HumanTR;
import Interpreter.HumanTW;
import Tuple.Tuple;
import Tuple.TupleComparator;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.PlainSelect;
import visitor.printQueryPlanVisitor;

/**
 * This class is used to sort externally.
 * Only used in Sort-Merge Join.
 * 
 * @author Hao Rong, hr335; Lini Tan lt398
 */
public class ExternalSortOperator extends Operator{
	
	private List<Tuple> sorted;
	private Operator childOp;
	private boolean isBinary = true;
	private String tempDir;
	private String specialname;
	private int pagenum;
	private BinaryTR btr;
	private HumanTR htr;
	private List order;
	private List<Column> schemaList;
	private int index;
	private List TableList;
	private HashMap map;
//	int bufferSize;
//	String jSortField; // the column name in the join condition
	
	public ExternalSortOperator(Operator op, List orderItem, int page) throws IOException{
		childOp = op;
		sorted = new LinkedList<>();
		order = orderItem;
		Catalog catalog = Catalog.getInstance();
		tempDir = catalog.getTempLocation();
		pagenum = page;
		specialname = UUID.randomUUID().toString();
		new File(tempDir+"/"+specialname).mkdir();
		pass0();
		mergePass(1);
		File folder = new File(tempDir+"/"+specialname);
		//merge finish
		if(folder.listFiles().length==0)return;
		File lastFile = folder.listFiles()[0];
		if (folder.listFiles().length>1){
			System.out.println("still merging");
			return;
		}
		if(isBinary==true){
			try {
				btr = new BinaryTR(lastFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			htr= new HumanTR(lastFile);
		}
		
		
		
//		Tuple sample = op.getNextTuple();
//		Object[] mapKeySet = sample.getTupleMap().keySet().toArray();
//		List order = new ArrayList();

		//jSortField = Column;
	}


	 /**
     * @param this method self-sorted the tuple in a page and prepare for the merge pass.
     */
	private void pass0() {
	// TODO Auto-generated method stub
		Tuple t = childOp.getNextTuple();
		TableList = t.getNameList();
		map = t.getTupleMap();
		if (t != null) {
			int MaxSize = 1024 / t.getTuple().size();
			int fileCount = 0 ;
			while (t != null) {
				sorted.add(t);
				t = childOp.getNextTuple();

				// if buffer is full, sort and write to file
				if (sorted.size() == MaxSize) {
					fileCount++;
					
					Collections.sort(sorted, new TupleComparator(order));
					
					String output = tempDir+"/"+specialname+"/pass0Output"+fileCount;
					//set a flag to distinct binary format and human format
					if (isBinary) {
						BinaryTW btw = new BinaryTW(output);
						for (Tuple tuple : sorted) {
							btw.WriteTuple(tuple);
						}
						btw.close();
					}else{
						File outputDir = new File(output);
						HumanTW htw;
						try {
							htw = new HumanTW(outputDir);
							for (Tuple tuple : sorted) {
								htw.WriteTuple(tuple);
							}
							htw.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					
					sorted.clear();
				}
			}
			//the page is full, write another page
			if(!sorted.isEmpty()){
				fileCount++;
				Collections.sort(sorted, new TupleComparator(order));
				
				String output = tempDir+"/"+specialname+"/pass0Output"+fileCount;
				//set a flag to distinct binary format and human format
				if (isBinary) {
					BinaryTW btw = new BinaryTW(output);
					for (Tuple tuple : sorted) {
						btw.WriteTuple(tuple);
					}
					btw.close();
				}else{
					File outputDir = new File(output);
					HumanTW htw;
					try {
						htw = new HumanTW(outputDir);
						for (Tuple tuple : sorted) {
							htw.WriteTuple(tuple);
						}
						htw.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
				sorted.clear();
			}
		}
	
}
	
	
	/**
	 * @param read all files in the the directory and do B-1 way merge recursively 
	 * until merge them all to a file
	 */
	private void mergePass(int i) throws IOException {
	// TODO Auto-generated method stub
		File folder = new File(tempDir+"/"+specialname);
		File[] AllfArray = folder.listFiles();
		// if only one file left, the merge is done
		if(AllfArray.length==1)return;
		
		
		ArrayList<ArrayList<File>> wholeFile = new ArrayList<ArrayList<File>>();
		
		ArrayList<File> fileList = new ArrayList<File>();
		for (int j = 0; j < AllfArray.length; j++){
			fileList.add(AllfArray[j]);
			if(fileList.size() == pagenum-1 || j == AllfArray.length-1){
				wholeFile.add(fileList);
				fileList = new ArrayList<File>();
			}
		}
		

		TupleComparator tCompare = new TupleComparator(order);
		
		if(isBinary){
			PriorityQueue<BinaryTR> rQueue = new PriorityQueue<BinaryTR>(pagenum-1, 
					new Comparator<BinaryTR>() {
		              	public int compare(BinaryTR i, BinaryTR j) {
		              		String iContent = i.peek();
		              		String jContent = j.peek();
		              		//System.out.println(TableList.toString());
		              		Tuple iTuple = new Tuple(iContent,TableList);
		              		iTuple.setTupleMap(map);
		              		Tuple jTuple = new Tuple(jContent,TableList);
		              		jTuple.setTupleMap(map);
		              		int res = tCompare.compare(iTuple, jTuple);
		              		return res;
		              	}
	            	});
			
			int fileNum = 0;
			for (ArrayList<File> fileBuffer : wholeFile){
				for (File f : fileBuffer){
					try {
						rQueue.add(new BinaryTR(f));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				fileNum++;
				String outputDir = tempDir+"/"+specialname+"/recursionOutput"+i+fileNum;
				BinaryTW btw = new BinaryTW(outputDir);
				try{
					while(!rQueue.isEmpty()){
						BinaryTR minReader = rQueue.poll();
						String sContent = minReader.ReadNextTuple();
						Tuple minTuple = new Tuple(sContent,TableList);
						minTuple.setTupleMap(map);
						btw.WriteTuple(minTuple);
						if(minReader.peek()==null){
							minReader.deleteFile();
						}else{
							rQueue.add(minReader);
						}
					}
				}finally{
					btw.close();
				}				
			}
			
			//call itself recursively
			mergePass(i+1);
			
		}else{
			
			PriorityQueue<HumanTR> rQueue = new PriorityQueue<HumanTR>(pagenum-1, 
					new Comparator<HumanTR>() {
		              	public int compare(HumanTR i, HumanTR j) {
		              		Tuple iTuple = i.peek();
		              		Tuple jTuple = j.peek();
		              		//System.out.println(TableList.toString());
//		              		Tuple iTuple = new Tuple(iContent,TableList);
//		              		iTuple.setTupleMap(map);
//		              		Tuple jTuple = new Tuple(jContent,TableList);
//		              		jTuple.setTupleMap(map);
		              		int res = tCompare.compare(iTuple, jTuple);
		              		return res;
		              	}
	            	});
			
			int fileNum = 0;
			for (ArrayList<File> fileBuffer : wholeFile){
				for (File f : fileBuffer){
					try {
						rQueue.add(new HumanTR(f));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				fileNum++;
				String outputDir = tempDir+"/"+specialname+"/recursionOutput"+i+fileNum;
				HumanTW htw = new HumanTW(new File(outputDir));
				try{
					while(!rQueue.isEmpty()){
						HumanTR minReader = rQueue.poll();
						String sContent = minReader.ReadNextTuple();
						Tuple minTuple = new Tuple(sContent,TableList);
						minTuple.setTupleMap(map);
						htw.WriteTuple(minTuple);
						if(minReader.peek()==null){
							minReader.deleteFile();
						}else{
							rQueue.add(minReader);
						}
					}
				}finally{
					htw.close();
				}				
			}
			
			//call itself recursively
			mergePass(i+1);


			
		}
	
}
	
	/**
	 * @param this method deletes the sorted file in the file system
	 * 
	 * **/
	public void deleteFileFolder(){
		File folder = new File(tempDir+"/"+specialname);
		folder.delete();
	}

	
	/**
	 * @return this method returns the next tuple
	 * 
	 * **/
	@Override
	public Tuple getNextTuple() {
		// TODO Auto-generated method stub
		if(isBinary){
			String tContent = btr.ReadNextTuple();
			if(tContent == null) return null;
			Tuple t = new Tuple(tContent,TableList);
			t.setTupleMap(map);
			this.index++;
			return t;
		}else{
			Tuple t;
			try {
				String tContent = htr.ReadNextTuple();
				if(tContent == null) return null;
				t = new Tuple(tContent,TableList);
				t.setTupleMap(map);
				this.index++;
				return t;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			return null;
		}
	}

	/**
	 * @param this method reset the operator
	 * 
	 * **/
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		if(isBinary){
			this.index = 0;
			btr.reset();
		}else{
			this.index = 0;
			try {
				htr.reset();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	/**
	 * @param this method recursively call get next tuple
	 * 
	 * **/
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

	/**
	 * @return this method return all the tuple and store in a list.
	 * 
	 * **/
	@Override
	public ArrayList<Tuple> getAllTuple() {
		// TODO Auto-generated method stub
		Tuple a =getNextTuple();
		ArrayList<Tuple> result = new ArrayList<Tuple>();
		while(a!= null){
			result.add(a);
			a =getNextTuple();
		}
		return result;
	}

	/**
	 * @param this method resets the tuple reader to a specified index
	 */
	public void reset(int index) {
		if(isBinary){
			this.index = index;
			btr.reset(index);
		}else{
			this.index = index;
			htr.reset(index);
		}
	}
	
	/**
	 * @return this method returns the current index
	 */
	public int getIndex() {
		return this.index;
	}


	@Override
	public void accept(printQueryPlanVisitor printQueryPlanVisitor) {
		printQueryPlanVisitor.visit(this);
	}
	
	public Operator getChild(){
		return this.childOp;
	}
}
