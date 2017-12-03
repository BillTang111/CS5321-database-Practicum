package Database_Catalog;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import net.sf.jsqlparser.schema.Column;

/**
 * This class is used for store index's information
 * 
 * @author Lini Tan lt398
 * */
public class IndexInfo {
	private Column column;
	private boolean clustered;
	private int order;
	private String indexPath;
	private String filePath;
	private int numLeaves;
	private final int SIZE = 4096;
	
	public IndexInfo(Column column, boolean isCluster, int order, String path) {
		this.column = column;
		this.clustered = isCluster;
		this.order = order;
		this.filePath = path;
		String fullName = column.getTable().getName() + "." + column.getColumnName();
		this.indexPath = filePath + "indexes/" + fullName;
		try {
			File testExsit = new File(indexPath);
			if(testExsit.exists()){
				System.out.println(indexPath + " exist");
				FileInputStream fin = new FileInputStream(indexPath);
	            FileChannel fc = fin.getChannel();
	            ByteBuffer bb = ByteBuffer.allocate(SIZE);
	    		bb.clear();
	    		fc.read(bb); // Read header page which stores metadata about the BPlusTree.
	    		bb.flip();
	    		numLeaves = bb.getInt(4);    		
	    		fc.close();
	    		fin.close();
			}else{
				System.out.println(indexPath + " not exist");
			}
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	/**
	 * This method is to get file path
	 * 
	 * @return the file path
	 * */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * This method is to get column
	 * 
	 * @return the column
	 * */
	public Column getColumn() {
		return column;
	}
	
	/**
	 * This method is to know whether the index is clustered or not
	 * 
	 * @return true if clustered, false if unclustered
	 * */
	public boolean isClustered() {
		return clustered;
	}
	
	/**
	 * This method is to get order
	 * @return order number
	 * */
	public int getOrder() {
		return order;
	}	
	
	/**
	 * This  method is to get index path
	 * 
	 * @return the index path
	 * */
	public String getIndexPath() {
		return this.indexPath;
	}
	
	/**
	 * This method is to get leave number
	 * 
	 * @return the leave number
	 * */
	public int getNumLeaves() {
		return this.numLeaves;
	}
	
	/**
	 * This method is to set alias
	 * 
	 * @param alias
	 * */
	public void setAlias(String alias) {
		this.column.getTable().setName(alias);
	}
	
	/**
	 * This method is to reset the table
	 * 
	 * @param the table you want to reset
	 * */
	public void reset(String tableName) {
		this.column.getTable().setName(tableName);
	}
}
