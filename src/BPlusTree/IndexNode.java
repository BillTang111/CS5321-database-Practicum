package BPlusTree;

import java.util.ArrayList;

/** This class is used to build indexNode.
 * @author Lini Tan lt398
 */
public class IndexNode {
	private ArrayList<Integer> key;
	private ArrayList<LeafNode> leafChildren;
	private ArrayList<IndexNode> indexChildren;
	private int address;
	private boolean IsUpperLayer;
	private int leafKey;
	
	/**
	 * Constructor for the first index layer whose children nodes are leaves
	 * 
	 */
	public IndexNode(ArrayList<Integer> keyList, ArrayList<LeafNode> leaf, int address, int leafKey){
		key = keyList;
		leafChildren = leaf;
		IsUpperLayer = false;
		this.address = address;
		this.leafKey = leafKey;
	}
	
	/**
	 * Constructor for other index layers
	 */
	public IndexNode(ArrayList<Integer> keyList, ArrayList<IndexNode> index, int address, boolean IsUpper, int leafKey){
		key = keyList;
		indexChildren = index;
		IsUpperLayer = true;
		this.address = address;
		this.leafKey = leafKey;
	}
	
	/**@return return the leaf Children of the indexNode*/
	public ArrayList<LeafNode> getLeafChildren(){
		return leafChildren;
	}
	
	/**@return return the index Children of the indexNode*/
	public ArrayList<IndexNode> getIndexChildren(){
		return indexChildren;
	}
	
	/**@return whether or not the index node is in the upper layer*/
	public boolean isUpperLayer(){
		return IsUpperLayer;
	}
	
	/**@return the key list of the node*/
	public ArrayList<Integer> getKeys(){
		return key;
	}

	/**@return the address of the node*/
	public int getAddress() {
		return address;
	}
	
	/**@return the leaf key of the node*/
	public int getLeafKey() {
		return this.leafKey;
	}
	
	
}
