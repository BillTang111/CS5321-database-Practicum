package BPlusTree;

/** The class is to build data entry. A data entry need to have pageid and tupleId
 * @author Lini Tan lt398
 */
public class DataEntry {
	private int pageId; 
	private int tupleId;
	
	public DataEntry(int pageId, int tupleId){
		this.pageId = pageId;
		this.tupleId = tupleId;
	}
	
	/**@return return the corresponding pageId*/
	public int getPageId(){
		return pageId;
	}
	
	/**@return return the corresponding tupleId*/
	public int getTupleId(){
		return tupleId;
	}
}
