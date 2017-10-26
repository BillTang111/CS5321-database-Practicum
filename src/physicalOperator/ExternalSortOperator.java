package physicalOperator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.PlainSelect;

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
	
//	int bufferSize;
//	String jSortField; // the column name in the join condition
	
	public ExternalSortOperator(Operator op, PlainSelect selectBody, int page) throws IOException{
		childOp = op;
		sorted = new LinkedList<>();
		order = selectBody.getOrderByElements();
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



	private void pass0() {
	// TODO Auto-generated method stub
		Tuple t = childOp.getNextTuple();
		if (t != null) {
		//	schemaList = t.getSchema();
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
	
	private void mergePass(int i) throws IOException {
	// TODO Auto-generated method stub
		File folder = new File(tempDir+"/"+specialname);
		//file Array hold all files
		File[] fileArray = folder.listFiles();
		if(fileArray.length==1)return;
		
		//store B-1 files in a list(buffer)
		//store all buffers into another list
		ArrayList<ArrayList<File>> allFiles = new ArrayList<ArrayList<File>>();
		
		ArrayList<File> fileList = new ArrayList<File>();
		for (int j = 0; j < fileArray.length; j++){
			fileList.add(fileArray[j]);
			if(fileList.size() == pagenum-1 || j == fileArray.length-1){
				allFiles.add(fileList);
				fileList = new ArrayList<File>();
			}
		}
		
		//put a buffer into a priority queue
		//poll the queue until it is empty and move on to the next buffer
		TupleComparator tcmp = new TupleComparator(order);
		
		if(isBinary){
			PriorityQueue<BinaryTR> readerQueue = new PriorityQueue<BinaryTR>(pagenum-1, 
					new Comparator<BinaryTR>() {
		              	public int compare(BinaryTR i, BinaryTR j) {
		              		int res = tcmp.compare(i.peek(), j.peek());
		              		return res;
		              	}
	            	});
			
			int fileCount = 0;
			for (ArrayList<File> fileBuffer : allFiles){
				//add all pages of a buffer into the queue
				for (File f : fileBuffer){
					try {
						readerQueue.add(new BinaryTR(f));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				fileCount++;
				String outputDir = tempDir+"/"+specialname+"/recursionOutput"+i+fileCount;
				BinaryTW btw = new BinaryTW(outputDir);
				try{
					while(!readerQueue.isEmpty()){
						BinaryTR smallestReader = readerQueue.poll();
						Tuple smallestTuple = smallestReader.ReadNextTuple2();
						btw.WriteTuple(smallestTuple);
						if(smallestReader.peek()==null){
							//reader queue is empty, delete the original file
							smallestReader.deleteFile();
						}else{
							readerQueue.add(smallestReader);
						}
					}
				}finally{
					btw.close();
				}				
			}
			
			//call itself recursively
			mergePass(i+1);
			
		}else{
			PriorityQueue<HumanTR> readerQueue = new PriorityQueue<HumanTR>(pagenum-1, 
					new Comparator<HumanTR>() {
		              	public int compare(HumanTR i, HumanTR j) {
		              		int res = tcmp.compare(i.peek(), j.peek());
		              		return res;
		              	}
	            	});
			
			int fileCount = 0;
			for (ArrayList<File> fileBuffer : allFiles){
				//add all pages of a buffer into the queue
				for (File f : fileBuffer){
					readerQueue.add(new HumanTR(f));
				}
				fileCount++;
				File outputDir = new File(tempDir+"/"+specialname+"/recursionOutput"+i+fileCount);
				HumanTW btw = new HumanTW(outputDir);
				try{
					while(!readerQueue.isEmpty()){
						HumanTR smallestReader = readerQueue.poll();
						Tuple smallestTuple = smallestReader.ReadNextTuple2();
						btw.WriteTuple(smallestTuple);
						if(smallestReader.peek()==null){
							//reader queue is empty, delete the original file
							smallestReader.deleteFile();
						}else{
							readerQueue.add(smallestReader);
						}
					}
				}finally{
					btw.close();
				}				
			}
			
			//call itself recursively	
				mergePass(i+1);


			
		}
	
}
	
	/**
	 * delete the sorted file in the file system
	 * 
	 * **/
	public void deleteFileFolder(){
		File folder = new File(tempDir+"/"+specialname);
		folder.delete();
	}

	@Override
	public Tuple getNextTuple() {
		// TODO Auto-generated method stub
		if(isBinary){
			Tuple t = btr.ReadNextTuple2();	
			this.index++;
			return btr==null ? null : t;
		}else{
			Tuple t;
			try {
				t = htr.ReadNextTuple2();
				this.index++;
				return htr==null ? null : t;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			return null;
		}
	}

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

}
