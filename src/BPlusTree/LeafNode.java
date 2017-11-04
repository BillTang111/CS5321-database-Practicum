package BPlusTree;

import java.util.List;
import java.util.TreeMap;


public class LeafNode {
	private TreeMap<Integer, List<DataEntry>> leafMap;
	private int address;
	private int num;
	
	public LeafNode(TreeMap<Integer, List<DataEntry>> leafMap2, int address ){
		this.address = address;
		leafMap = leafMap2;
		num = leafMap2.size();
	}
	
	public TreeMap<Integer,List<DataEntry>> getMap(){
		return leafMap;
	}

	public int getNum() {
		return num;
	}

	public int getAddress() {
		return address;
	}
	
	
}
