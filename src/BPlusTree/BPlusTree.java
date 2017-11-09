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
	private boolean isCluster;
	private String tableName;
	
	
	public BPlusTree(boolean clusterOrNot, String tableName, String columnName, int order, String location){
		fileLocation = location;
		this.order  = order;
		column = tableName + "." + columnName;
		serializer = new Serializer(location+"indexes/"+column);
		leafList = new ArrayList<LeafNode>();
		leafList = buildLeafLayer(tableName, column, clusterOrNot);
		root = buildIndexLayers();
		serializer.writeNextNode(root);
		serializer.writeHeadPage(countSize, leafList.size(), order);
		serializer.close();
	}
	
	private void cluster(String column, String inputPath, String tableName){
		File input = new File(inputPath);
		try {
			BinaryTR btr = new BinaryTR(input);
			ArrayList<Tuple> tupleList = new ArrayList();
			String tupleContent = btr.ReadNextTuple();
			if(tupleContent == null) return;
			ArrayList tableList = new ArrayList();
			tableList.add(tableName);
			//System.out.println(tableName);
			Tuple tuple = new Tuple(tupleContent, tableList);
			while(tuple != null){
				tupleList.add(tuple);
				tupleContent = btr.ReadNextTuple();
				if(tupleContent == null) break;
				tableList = new ArrayList();
				tableList.add(tableName);
				tuple = new Tuple(tupleContent, tableList);
			}
			ArrayList orderList = new ArrayList();
			orderList.add(column);
			//System.out.println(tupleList.toString());
			System.out.println(column);
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
	private ArrayList<LeafNode> buildLeafLayer(String tableName, String column, boolean clustered) {
		if(isCluster == true){
		String inputPath = fileLocation+"data/"+tableName;
		cluster(column, inputPath, tableName);
	}
		File table = new File(fileLocation + "data/" + tableName);
		BinaryTR btr;
		try {
			btr = new BinaryTR(table);
			//store all data we need
			TreeMap<Integer, List<DataEntry>> allData = new TreeMap<>();
			String tContent = btr.ReadNextTuple();
			int key;
			while (tContent != null) {
				ArrayList tableList = new ArrayList();
				tableList.add(tableName);
				Tuple tuple = new Tuple(tContent, tableList);
				ArrayList tupleList = tuple.getTuple();
				String columnValue = (String) tupleList.get((int) tuple.getTupleMap().get(column));
				key = Integer.parseInt(columnValue);
				int pageId = btr.getPageId();
				int tupleId = btr.getTupleId();
				if (allData.containsKey(key))
					allData.get(key).add(new DataEntry(pageId, tupleId));
				else {
					ArrayList<DataEntry> temp = new ArrayList<DataEntry>();
					temp.add(new DataEntry(pageId, tupleId));
					allData.put(key, temp);
				}
				tContent = btr.ReadNextTuple();
			}
			
			if(allData.isEmpty()) return null;

			// split all data into different leaf nodes based on the order 
			TreeMap<Integer, List<DataEntry>> tempMap = new TreeMap<Integer, List<DataEntry>>();
			for (Entry<Integer, List<DataEntry>> entry : allData.entrySet()) {
				if (tempMap.size() < order * 2) {
					tempMap.put(entry.getKey(), entry.getValue());
				} else {
					countSize += 1;
					LeafNode leaf = new LeafNode(tempMap, countSize);
					leafList.add(leaf);
					serializer.writeNextNode(leaf);
					tempMap = new TreeMap<>();
					tempMap.put(entry.getKey(), entry.getValue());
				}
			}

			// handle last two nodes
			//int remaining;
			int remaining = tempMap.size();
			LeafNode n = null;
			if (leafList.isEmpty()) {
				countSize += 1;
				n = new LeafNode(tempMap, countSize);
			} else if (remaining < order){
				remaining += 2 * order;
				int k = remaining / 2;
				//retrieve data entry from the last node so that it contains only k entries.
				TreeMap<Integer, List<DataEntry>> lastMap = leafList.get(leafList.size() - 1).getMap();
				LeafNode leaf = leafList.remove(leafList.size() - 1);
				serializer.GoPage(leaf.getAddress());
				for (int i = 0; i < 2 * order - k; i++) {
					int lastKey = lastMap.lastKey();
					List<DataEntry> lastEntryList = lastMap.get(lastKey);
					tempMap.put(lastKey, lastEntryList);
					lastMap.remove(lastKey);
				}
				n = new LeafNode(lastMap, countSize);
				leafList.add(n);
				serializer.writeNextNode(n);
				countSize += 1;
				n = new LeafNode(tempMap, countSize);
			} else {
				countSize += 1;
				n = new LeafNode(tempMap, countSize);
			}
			leafList.add(n);
			serializer.writeNextNode(n);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		return leafList;
	}
	
	/**
	 * Build the first layer of index nodes and then recursively build the upper layer
	 */
	private IndexNode buildIndexLayers() {
		// build the first index layer
		ArrayList<IndexNode> firstIndexLayer = new ArrayList<IndexNode>();

		ArrayList<LeafNode> leafChildren = new ArrayList<LeafNode>();
		ArrayList<Integer> keyList = new ArrayList<Integer>();
		if (leafList.isEmpty()) {
			return null;
		}
		//put the leaf in the leaf list to the leaf children
		for (LeafNode leaf : leafList) {
			keyList.add(leaf.getMap().firstKey());
			leafChildren.add(leaf);
			if (keyList.size() == 2 * order + 1) {
				int leafKey = keyList.remove(0);
				countSize += 1;
				IndexNode node = new IndexNode(keyList, leafChildren, countSize, leafKey);
				firstIndexLayer.add(node);
				serializer.writeNextNode(node);
				leafChildren = new ArrayList<LeafNode>();
				keyList = new ArrayList<Integer>();
			}
		}

		// handle the last two nodes
		if (firstIndexLayer.isEmpty()) {
			int leafKey = keyList.get(0);
			if (keyList.size() > 1) {
				keyList.remove(0);
			}
			countSize += 1;
			IndexNode n = new IndexNode(keyList, leafChildren, countSize, leafKey);
			firstIndexLayer.add(n);
			serializer.writeNextNode(n);
		} else if (keyList.size() > 0 && keyList.size() < order + 1) {
			IndexNode tempIndexNode = firstIndexLayer.get(firstIndexLayer.size() - 1);
			firstIndexLayer.remove(firstIndexLayer.size() - 1);
			serializer.GoPage(tempIndexNode.getAddress());
			ArrayList<LeafNode> firstChildren = tempIndexNode.getLeafChildren();
			ArrayList<LeafNode> secondChildren = new ArrayList<LeafNode>();
			ArrayList<Integer> keyList1 = new ArrayList<Integer>();
			ArrayList<Integer> keyList2 = new ArrayList<Integer>();
			firstChildren.addAll(leafChildren);
			int k = firstChildren.size();
			while (firstChildren.size() > k / 2 ) {
				secondChildren.add(firstChildren.get(k / 2 ));
				keyList2.add(firstChildren.get(k / 2 ).getMap().firstKey());
				firstChildren.remove(k / 2 );
			}
			for (LeafNode ln : firstChildren) {
				keyList1.add(ln.getMap().firstKey());
			}

			int leafKey1 = keyList1.remove(0);
			int leafKey2 = keyList2.remove(0);
			// write the result to the last two nodes
			IndexNode node = new IndexNode(keyList1, firstChildren, countSize, leafKey1);
			firstIndexLayer.add(node);
			serializer.writeNextNode(node);
			countSize += 1;
			node = new IndexNode(keyList2, secondChildren, countSize, leafKey2);
			firstIndexLayer.add(node);
			serializer.writeNextNode(node);

		} else if (keyList.size() != 0) {
			int leafKey = keyList.remove(0);
			countSize += 1;
			IndexNode in = new IndexNode(keyList, leafChildren, countSize, leafKey);
			firstIndexLayer.add(in);
			serializer.writeNextNode(in);
		}
		return buildUpperLayers(firstIndexLayer);
	}
	
	
	/**
	 * @param IndexLayer:
	 *            arrayList of index nodes build the upper level from the input
	 *            arrayList call itself recursively until reaches the root
	 */
	private IndexNode buildUpperLayers(ArrayList<IndexNode> IndexLayer) {
		ArrayList<IndexNode> output = new ArrayList<IndexNode>();

		ArrayList<IndexNode> indexChildren = new ArrayList<IndexNode>();

		ArrayList<Integer> keyList = new ArrayList<Integer>();
		for (IndexNode index : IndexLayer) {
			indexChildren.add(index);
			keyList.add(index.getLeafKey());
			if (keyList.size() == 2 * order + 1) {
				int leafKey = keyList.remove(0);
				countSize += 1;
				IndexNode n = new IndexNode(keyList, indexChildren, countSize, true, leafKey);
				output.add(n);
				serializer.writeNextNode(n);
				keyList = new ArrayList<Integer>();
				indexChildren = new ArrayList<IndexNode>();
			}
		}
		if (output.isEmpty() && !keyList.isEmpty()) {
			int leafKey = keyList.get(0);
			if (keyList.size() > 1) {
				keyList.remove(0);
			}
			countSize += 1;
			return new IndexNode(keyList, indexChildren, countSize, true, leafKey);
		} else if (!keyList.isEmpty() && keyList.size() - 1 < order) {
			IndexNode tempNode = output.get(output.size() - 1);
			ArrayList<IndexNode> firstChildren = tempNode.getIndexChildren();

			firstChildren.addAll(indexChildren);
			output.remove(output.size() - 1);
			serializer.GoPage(tempNode.getAddress());
			// split firstChildren to make two new nodes
			int k = firstChildren.size();
			ArrayList<IndexNode> secondChildren = new ArrayList<IndexNode>();
			ArrayList<Integer> keyList1 = new ArrayList<Integer>();
			ArrayList<Integer> keyList2 = new ArrayList<Integer>();
			while (firstChildren.size() > k / 2) {
				secondChildren.add(firstChildren.get(k / 2));
				keyList2.add(firstChildren.get(k / 2).getLeafKey());
				firstChildren.remove(k / 2);
			}
			for (IndexNode in : firstChildren) {
				keyList1.add(in.getLeafKey());
			}
			int leafKey1 = keyList1.remove(0);
			int leafKey2 = keyList2.remove(0);
			IndexNode n = new IndexNode(keyList1, firstChildren, countSize, true, leafKey1);
			output.add(n);
			serializer.writeNextNode(n);
			countSize += 1;
			n = new IndexNode(keyList2, secondChildren, countSize, true, leafKey2);
			output.add(n);
			serializer.writeNextNode(n);

		} else if (!keyList.isEmpty()) {
			int leafKey = keyList.remove(0);
			countSize += 1;
			IndexNode n = new IndexNode(keyList, indexChildren, countSize, true, leafKey);
			output.add(n);
			serializer.writeNextNode(n);
		}
		return buildUpperLayers(output);
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
