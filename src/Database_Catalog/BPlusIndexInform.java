package Database_Catalog;

import net.sf.jsqlparser.schema.Column;

/** A class of objects to store B Plus tree's index information
 * 
 * @author benzhangtang
 *
 */

public class BPlusIndexInform {
	private String indexPath;
	private int order;
	private String column;
	private boolean isClustered;
	
	//class constructor creating data structure to store index inform
	public BPlusIndexInform (String column, boolean isClustered, int order, String indexPath) {
	this.column = column;
	this.isClustered=isClustered; 
	this.order=order;
	this.indexPath=indexPath;
}

	//get method to get fields
	public String getIndexPath() {
		return indexPath;
	}
	
	
	public String getColumn() {
		return column;
	}
	
	public int getOrder() {
		return order;
	}
	public boolean isClustered() {
		return isClustered;
	} 
	
}