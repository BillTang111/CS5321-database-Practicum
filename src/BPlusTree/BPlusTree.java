package BPlusTree;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;

import Database_Catalog.Catalog;
import Interpreter.BinaryTR;
import Interpreter.BinaryTW;
import Tuple.Tuple;
import Tuple.TupleComparator;
import net.sf.jsqlparser.schema.Column;
import physicalOperator.*;

public class BPlusTree {
	private String fileLocation;
	private String column;
	private IndexNode root;
	private ArrayList<LeafNode> leafList;
	private int order;
	private int pageSize;
	private int countSize;
	private Serializer serializer;
	
	
	public BPlusTree(boolean clusterOrNot, String tableName, String columnName, int order, String location){
		fileLocation = location;
		this.order  = order;
		column = tableName + "." + columnName;
		serializer = new Serializer(location+"indexes/"+column);
		leafList = new ArrayList<LeafNode>();
		//Add support for building both clustered and unclustered indexes
		if(clusterOrNot == true){
			String inputPath = location+"data/"+tableName;
			cluster(column, inputPath, tableName);
		}
	//	leafList = buildLeafLayer(tableName, column);
		//root = buildIndexLayers();
//		serializer.writeNextNode(root);
//		serializer.writeHeadPage(size, leafNodes.size(), D);
//		serializer.close();
	}
	
	private void cluster(String column, String inputPath, String tableName){
		File input = new File(inputPath);
		try {
			BinaryTR btr = new BinaryTR(input);
			ArrayList<Tuple> tupleList = new ArrayList();
			String tupleContent = btr.ReadNextTuple();
			ArrayList tableList = new ArrayList();
			tableList.add(tableName);
			Tuple tuple = new Tuple(tupleContent, tableList);
			while(tuple != null){
				tupleList.add(tuple);
				tupleContent = btr.ReadNextTuple();
				tableList = new ArrayList();
				tableList.add(tableName);
				tuple = new Tuple(tupleContent, tableList);
			}
			ArrayList orderList = new ArrayList();
			orderList.add(column);
			Collections.sort(tupleList, new TupleComparator(orderList));
			btr.deleteFile();
			BinaryTW btw = new BinaryTW(fileLocation + "data/" + tableName);
			for (Tuple t : tupleList) {
				btw.WriteTuple(t);
			}
			btw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * build the very bottom layer of the tree(leaves)
	 * 
	 * @param tableName
	 * @param column
	 * @return an array list to store all leaf nodes
	 */
	private ArrayList<LeafNode> buildLeafLayer(String tableName, String column) {
		File table = new File(fileLocation + "data/" + tableName);
		TreeMap<Integer, List<DataEntry>> allMap = new TreeMap<>();
		try {
			BinaryTR btr = new BinaryTR(table);
			// build a tree map to store all key/dataEntry pairs in the corresponding column
			String tContent = btr.ReadNextTuple();
			int key = 0;
			while (tContent != null) {
				ArrayList tableList = new ArrayList();
				tableList.add(tableName);
				Tuple tuple = new Tuple(tContent, tableList);
				ArrayList tupleList = tuple.getTuple();
				key = (int) tupleList.get((int) tuple.getTupleMap().get(column));
				int pageId = btr.getPageId();
				int tupleId = btr.getTupleId();
				if (allMap.containsKey(key))
					allMap.get(key).add(new DataEntry(pageId, tupleId));
				else {
					ArrayList<DataEntry> newList = new ArrayList<DataEntry>();
					newList.add(new DataEntry(pageId, tupleId));
					allMap.put(key, newList);
				}
				tContent = btr.ReadNextTuple();
			}
			//split the allmap into different leafNode
			int dataEntryNum = 0;
			int leafNum = allMap.size()/(2*order);
			if(allMap.size()%(2*order)!=0){
				leafNum++;
				//if leave us with < d data entries for the last leaf node
				if(allMap.size()%(2*order)<order){
					TreeMap<Integer, List<DataEntry>> leafMap = new TreeMap<Integer, List<DataEntry>>();
					for (Entry<Integer, List<DataEntry>> entry : allMap.entrySet()) {
						if(leafList.size()>=leafNum-2){
							int remainder = leafMap.size();
							int restData = allMap.size()-dataEntryNum;
							//the second to last leaf node should take
							int secondLastNum = (restData+remainder)/2;
							//build second to last leaf
							if(remainder<=secondLastNum){
								leafMap.put(entry.getKey(), entry.getValue());
								remainder++;
								dataEntryNum++;
								//store the second to last leaf, start the last leaf
							}else if(remainder == secondLastNum+1){
								countSize++;
								LeafNode leaf = new LeafNode(leafMap, countSize);
								leafList.add(leaf);
								//serializer.writeNextNode(leaf);
								leafMap = new TreeMap<>();
								leafMap.put(entry.getKey(), entry.getValue());
								remainder++;
								dataEntryNum++;
							}else{
								leafMap.put(entry.getKey(), entry.getValue());
								remainder++;
								dataEntryNum++;
								if(dataEntryNum == allMap.size()){
									countSize++;
									LeafNode leaf = new LeafNode(leafMap, countSize);
									leafList.add(leaf);
									//serializer.writeNextNode(leaf);
								}
							}
						}else{
						//when a leaf is not full, fill it
						if(leafMap.size()<=2*order){
							leafMap.put(entry.getKey(), entry.getValue());
							dataEntryNum++;
						}else{
						//if full; add the leaf to the leafList, build new leaf and check if it's reaching the last two leaf
						countSize++;
						LeafNode leaf = new LeafNode(leafMap, countSize);
						leafList.add(leaf);
						//serializer.writeNextNode(leaf);
						leafMap = new TreeMap<>();
						leafMap.put(entry.getKey(), entry.getValue());
						dataEntryNum++;
						}
					}
					}
				}else{// if not, just build leaf in order
					TreeMap<Integer, List<DataEntry>> leafMap = new TreeMap<Integer, List<DataEntry>>();
					for (Entry<Integer, List<DataEntry>> entry : allMap.entrySet()) {
						//when a leaf is not full, fill it
						if(leafMap.size()<=2*order){
							leafMap.put(entry.getKey(), entry.getValue());
							dataEntryNum++;
							if(dataEntryNum == allMap.size()){
								countSize++;
								LeafNode leaf = new LeafNode(leafMap, countSize);
								leafList.add(leaf);
								//serializer.writeNextNode(leaf);
								}
						}else{
						//if full; add the leaf to the leafList, build new leaf and check if it's reaching the last two leaf
						countSize++;
						LeafNode leaf = new LeafNode(leafMap, countSize);
						leafList.add(leaf);
						//serializer.writeNextNode(leaf);
						leafMap = new TreeMap<>();
						leafMap.put(entry.getKey(), entry.getValue());
						dataEntryNum++;
						}
					}
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return leafList;
	}
	
	/**
	 * Build the first layer of index nodes at the beginning and then build the
	 * upper levels recursively
	 */
	private IndexNode buildIndexLayers() {
		
		return root;
	}
	
	
	
	/**
	 * get the root of this tree
	 * @return index node: root
	 */
	public IndexNode getRoot(){
		return root;
	}
	
	/**
	 * get all leaf nodes
	 * 
	 * @return an arrayList of leaf node
	 */
	public ArrayList<LeafNode> getAllChildren() {
		return leafList;
	}
	
	
}
