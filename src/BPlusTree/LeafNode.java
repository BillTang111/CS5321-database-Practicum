package BPlusTree;

import java.util.List;
import java.util.TreeMap;

/**This class is to build leaf node for b+ tree
 * 
 * author Lini Tan lt398
 * */
public class LeafNode {
	private TreeMap<Integer, List<DataEntry>> leafMap;
	private int address;
	private int num;
	

	public LeafNode(TreeMap<Integer, List<DataEntry>> leafMap2, int address ){
		this.address = address;
		leafMap = leafMap2;
		num = leafMap2.size();
	}
	
	/**@return the map of the leaf node*/
	public TreeMap<Integer,List<DataEntry>> getMap(){
		return leafMap;
	}

	/**@return the num of the leaf node*/
	public int getNum() {
		return num;
	}

	/**@return the address of the leaf node*/
	public int getAddress() {
		return address;
	}
	
	
}
