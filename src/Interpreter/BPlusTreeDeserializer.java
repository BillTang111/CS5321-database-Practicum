package Interpreter;


import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.LinkedList;
import java.util.List;

import BPlusTree.DataEntry;
import Database_Catalog.BPlusIndexInform;

/** A class to deserialize B Plus Tree index to find matched data entries
 * as a list of data entries in case of unclustered index or as a single data entry 
 * in case of clustered index.
 * @author benzhangtang
 */


public class BPlusTreeDeserializer {
	private FileInputStream fis;
	private FileChannel fc;
	private ByteBuffer bb;
	private int RootAddress;
	private int numleaves;
	public int PageSize = 4096;

	public BPlusTreeDeserializer (BPlusIndexInform indexInform) throws IOException {
		fis = new FileInputStream (indexInform.getIndexPath());
		fc = fis.getChannel();
		//allocate buffer to load the header page
		bb = ByteBuffer.allocate(PageSize);
		bb.clear();
		fc.read(bb);
		bb.flip(); //start a sequence of "gets"
		RootAddress = bb.getInt(0);
		numleaves = bb.getInt(4);
	}

	/*
	 * [FIndPageNum] is a helper method that traverse the tree to find the 
	 * correct page number for the matched key.
	 * @Return: matched leaf node's page number  
	 * */
	private int FIndPageNum(Long key) {
		int PageNum = RootAddress; 
		//add this
		try {
			fc.position((long)(PageSize * PageNum));
			//end add
			//bb = ByteBuffer.allocate(PageSize*PageNum);
			bb = ByteBuffer.allocate(PageSize);
			bb.clear();
			fc.read(bb);
			bb.flip(); //start a sequence of "gets"
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		boolean isLeaf = false;
		//start searching from the top of the tree 
		while (!isLeaf) {
			int numOfKeys = bb.getInt(4);
			int child=0;
			for (child=0; child<numOfKeys; child++) {
				int childKey = bb.getInt(8+4*child);
				if (childKey >key.intValue()) break;
			}
			//through page address, go to next page 
			//PageNum = bb.getInt(8+child*4);
			PageNum = bb.getInt(8+4*numOfKeys+4*child);

			try {
				fc.position((long)(PageNum*PageSize));
				bb=ByteBuffer.allocate(PageSize);
				bb.clear();
				fc.read(bb);
				bb.flip();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			isLeaf = (bb.getInt(0)==0);
		}
		return PageNum;
	}

	/**[getRids] returns the list of rids or data entries that are between the low and high key
	 * 
	 * @param lowkey
	 * @param highkey
	 * @return List<DataEntry>
	 */
	//try to debug
	public List<DataEntry> getEntries(Long lowkey, Long highkey){
		List<DataEntry> entriesList = new LinkedList<>();
		if (lowkey != null) {
			int	PageNum = FIndPageNum (lowkey);
			boolean isLeaf = (bb.getInt()==0);
			while(isLeaf){
				int numEntries = bb.getInt();
				for (int i=0; i<numEntries; i++) {
					int currentKey = bb.getInt();
					if (currentKey>lowkey.intValue()) {
						if (highkey != null) {
							if (currentKey>highkey.intValue()) {
								return entriesList;
							}
						}
						int numRids = bb.getInt();
						for (int k=0; k<numRids; k++) {
							int pageId =  bb.getInt();
							int tupleId = bb.getInt();
							DataEntry dataTuple = new DataEntry(pageId,tupleId);
							entriesList.add(dataTuple);
						}
					}else{
						int numRids = bb.getInt();
						for (int k=0; k<numRids; k++) {
							int pageId =  bb.getInt();
							int tupleId = bb.getInt();
						}
					}
				}// end of for loop
				PageNum++;
				if (numEntries > numleaves) {
					return entriesList;
				}else { //read next page
					try {
						fc.position(numEntries*PageSize);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					bb=ByteBuffer.allocate(PageSize);
					bb.clear();
					try {
						fc.read(bb);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					bb.flip();}

				isLeaf = (bb.getInt(0)==0);
				
			}
		}else{// if low key is null
			int PageNum = 1; //start reading from the leftmost leaf node
			for (PageNum=1; PageNum<=numleaves; PageNum++) {
				try {
					fc.position(PageNum*PageSize);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				bb=ByteBuffer.allocate(PageSize);
				bb.clear();
				try {
					fc.read(bb);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				bb.flip();
				//debug here
				//bb.getInt(); //skip the flag
				boolean isLeaf = (bb.getInt()==0);
				int numEntry = bb.getInt();
				for (int i =0; i<numEntry; i++) {
					int currentKey=bb.getInt();
					System.out.println("I am lowkey: "+lowkey);
					//if (currentKey>lowkey.intValue()) {
					//					if (currentKey>=highkey.intValue()) {
					if (currentKey>highkey.intValue()) {
						return entriesList;
					} else {
						int numRids = bb.getInt();
						for (int k=0; k<numRids; k++) {
							int pageId =  bb.getInt();
							int tupleId = bb.getInt();
							DataEntry dataTuple = new DataEntry(pageId,tupleId);
							entriesList.add(dataTuple);
						}
					}
				}
			}
		}// end of else
		return entriesList;
	}
	
//	public List<DataEntry> getEntries(Long lowkey, Long highkey){
//		List<DataEntry> entriesList = new LinkedList<>();
//		if (lowkey != null) {
//			int	PageNum = FIndPageNum (lowkey);
//			boolean isLeaf = (bb.getInt()==0);
//			int	numEntries = 10000; //randomly assign an initial number of entries 
//			while (isLeaf && numEntries!=0) {
//				numEntries = bb.getInt();
////				System.out.println("i am right here");
////				System.out.println("num: "+numEntries);
//				for (int i=0; i<numEntries; i++) {
//					int currentKey = bb.getInt();
////					System.out.println("!!! low key: " + lowkey);
////					System.out.println("!!! current key: " + currentKey);
////					System.out.println("!!! high key: " + highkey);
//
//						if (currentKey>lowkey.intValue()) {
//							if (highkey != null) {
//								//debug here
//							if (currentKey>highkey.intValue()) {
//								return entriesList;
//							}else { // low_key < k < high_key
//								int numRids = bb.getInt();
//								for (int k=0; k<numRids; k++) {
//									int pageId =  bb.getInt();
//									int tupleId = bb.getInt();
//									DataEntry dataTuple = new DataEntry(pageId,tupleId);
//									entriesList.add(dataTuple);
//								}
//							}
//						}	
//					} else { //current key value is lower than the data entry's key
//						int numRids = bb.getInt();
//						for (int k=0; k<numRids; k++) {
//							int pageId =  bb.getInt();
//							int tupleId = bb.getInt();
//						}
//					} 
//				}
//
//				PageNum++;
//				if (numEntries > numleaves) {
//					return entriesList;
//				}else { //read next page
//					try {
//						fc.position(numEntries*PageSize);
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					bb=ByteBuffer.allocate(PageSize);
//					bb.clear();
//					try {
//						fc.read(bb);
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					bb.flip();}
//
//				isLeaf = (bb.getInt(0)==0);
//			}
//		}else {//if the lowkey is null
//			int PageNum = 1; //start reading from the leftmost leaf node
//			for (PageNum=1; PageNum<=numleaves; PageNum++) {
//				try {
//					fc.position(PageNum*PageSize);
//				} catch (IOException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//				bb=ByteBuffer.allocate(PageSize);
//				bb.clear();
//				try {
//					fc.read(bb);
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				bb.flip();
//				//debug here
//				//bb.getInt(); //skip the flag
//				boolean isLeaf = (bb.getInt()==0);
//				int numEntry = bb.getInt();
//				for (int i =0; i<numEntry; i++) {
//					int currentKey=bb.getInt();
//					System.out.println("I am lowkey: "+lowkey);
//					//if (currentKey>lowkey.intValue()) {
//					//					if (currentKey>=highkey.intValue()) {
//					if (currentKey>highkey.intValue()) {
//						return entriesList;
//					} else {
//						int numRids = bb.getInt();
//						for (int k=0; k<numRids; k++) {
//							int pageId =  bb.getInt();
//							int tupleId = bb.getInt();
//							DataEntry dataTuple = new DataEntry(pageId,tupleId);
//							entriesList.add(dataTuple);
//						}
//					}
//				}
//			}
//		}
//		return entriesList;
//	}

	/**[getLeftMostEntry] is a method designed for clustered index case. 
	 * 
	 * @param lowkey
	 * @return the leftmost data entry that satisfies the searching condition
	 */
	public DataEntry getLeftMostEntry (Long lowkey, Long highkey) {
		if (lowkey != null) {
			try {
				int PageNum = FIndPageNum(lowkey);
				boolean isLeaf = bb.getInt()==0;

				while (isLeaf) {
					int numEntries = bb.getInt();

					for (int i=0; i<numEntries; i++) {
						int currentKey = bb.getInt();
						if (highkey != null) {

							if (currentKey>lowkey.intValue()) {
								if (currentKey>highkey.intValue()) {
									return null;
								} else {
									int numRids = bb.getInt();
									int pageId =  bb.getInt();
									int tupleId = bb.getInt();
									return new DataEntry(pageId,tupleId);
								}
							}	
						} 
					}
				}
				PageNum++;
				if (PageNum > numleaves) {
					return null;
				}else { //read next page
					fc.position(PageNum*PageSize);
					bb=ByteBuffer.allocate(PageSize);
					bb.clear();
					fc.read(bb);
					bb.flip();
					isLeaf = (bb.getInt(0)==0);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}else {//if the lowkey is null
			int PageNum = 1; //start reading from the leftmost leaf node
			try {
				fc.position(PageNum*PageSize);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			bb=ByteBuffer.allocate(PageSize);
			bb.clear();
			try {
				fc.read(bb);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			bb.flip();
			int current_key = bb.getInt(8); //get the key
			if (current_key >highkey) {
				return null;
			}
			int pageId =bb.getInt(4*4);
			int tupleId = bb.getInt(4*5);
			return new DataEntry(pageId,tupleId);
		}
	}
}



