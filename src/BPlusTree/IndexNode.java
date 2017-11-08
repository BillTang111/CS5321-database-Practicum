package BPlusTree;

import java.util.ArrayList;

public class IndexNode {
	private ArrayList<Integer> key;
	private ArrayList<LeafNode> leafChildren;
	private ArrayList<IndexNode> indexChildren;
	private int address;
	private boolean IsUpperLayer;
	private int leafKey;
	
	/**
	 * Constructor for the first index layer whose children nodes are leaves
	 * @param keyList: key list of the node
	 * @param leaf: list of LeafNode
	 * @param address: the address of this node;
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
	 * @param keyList: key list of the node
	 * @param leaf: list of LeafNode
	 * @param address: the address of this node;
	 * @param isUpper: to distinguish from the constructor above. Indicate whether it is the upper layer index;
	 */
	public IndexNode(ArrayList<Integer> keyList, ArrayList<IndexNode> index, int address, boolean IsUpper, int leafKey){
		key = keyList;
		indexChildren = index;
		IsUpperLayer = true;
		this.address = address;
		this.leafKey = leafKey;
	}
	
	public ArrayList<LeafNode> getLeafChildren(){
		return leafChildren;
	}
	
	public ArrayList<IndexNode> getIndexChildren(){
		return indexChildren;
	}
	
	public boolean isUpperLayer(){
		return IsUpperLayer;
	}
	
	public ArrayList<Integer> getKeys(){
		return key;
	}

	public int getAddress() {
		return address;
	}
	
	public int getLeafKey() {
		return this.leafKey;
	}
	
	
}
