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
		leafList = buildLeafLayer(tableName, column);
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
				String columnValue = (String) tupleList.get((int) tuple.getTupleMap().get(column));
				key = Integer.parseInt(columnValue);
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
								serializer.writeNextNode(leaf);
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
									serializer.writeNextNode(leaf);
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
						serializer.writeNextNode(leaf);
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
								serializer.writeNextNode(leaf);
								}
						}else{
						//if full; add the leaf to the leafList, build new leaf and check if it's reaching the last two leaf
						countSize++;
						LeafNode leaf = new LeafNode(leafMap, countSize);
						leafList.add(leaf);
						serializer.writeNextNode(leaf);
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
		// build the first index layer
				ArrayList<IndexNode> firstIndexLayer = new ArrayList<IndexNode>();

				ArrayList<LeafNode> leafChildren = new ArrayList<LeafNode>();
				ArrayList<Integer> keyList = new ArrayList<Integer>();
				if (leafList.isEmpty()) {
					return null;
				}
				for (LeafNode ln : leafList) {
					keyList.add(ln.getMap().firstKey());
					leafChildren.add(ln);
					if (keyList.size() == 2 * order + 1) {
						keyList.remove(0);
						countSize += 1;
						IndexNode n = new IndexNode(keyList, leafChildren, countSize);
						firstIndexLayer.add(n);
						serializer.writeNextNode(n);
						leafChildren = new ArrayList<LeafNode>();
						keyList = new ArrayList<Integer>();
					}
				}

				// handle last one or two node
				if (firstIndexLayer.isEmpty()) {
					if (keyList.size() > 1) {
						keyList.remove(0);
					}
//					else {
//						//leafChildren.add(0, null);
//					}
					countSize += 1;
					IndexNode n = new IndexNode(keyList, leafChildren, countSize);
					firstIndexLayer.add(n);
					serializer.writeNextNode(n);
				} else if (keyList.size() > 0 && keyList.size() < order + 1) {

					// pop the last node from the layer
					IndexNode tempNode = firstIndexLayer.get(firstIndexLayer.size() - 1);
					firstIndexLayer.remove(firstIndexLayer.size() - 1);
					serializer.GoPage(tempNode.getAddress());
					ArrayList<LeafNode> firstChildren = tempNode.getLeafChildren();
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
					keyList1.remove(0);
					keyList2.remove(0);
					IndexNode n = new IndexNode(keyList1, firstChildren, countSize);
					firstIndexLayer.add(n);
					serializer.writeNextNode(n);
					countSize += 1;
					n = new IndexNode(keyList2, secondChildren, countSize);
					firstIndexLayer.add(n);
					serializer.writeNextNode(n);

				} else if (keyList.size() != 0) {// D <= size <= 2D
					keyList.remove(0);
					countSize += 1;
					firstIndexLayer.add(new IndexNode(keyList, leafChildren, countSize));
				}
				// after building the first layer, build the rest of the index
				// layers recursively until reach to root;
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
			keyList.add(index.getKeys().get(0));
			if (keyList.size() == 2 * order + 1) {
				keyList.remove(0);
				countSize += 1;
				IndexNode n = new IndexNode(keyList, indexChildren, countSize, true);
				output.add(n);
				serializer.writeNextNode(n);
				keyList = new ArrayList<Integer>();
				indexChildren = new ArrayList<IndexNode>();
			}
		}
		// if the last node underflow, pop the last node from output
		// merge the children between the two nodes, split them.
		// generate new keys for the new sets of children
		// add both new nodes to output
		if (output.isEmpty() && !keyList.isEmpty()) {
			if (keyList.size() > 1) {
				keyList.remove(0);
			} else {
				//indexChildren.add(0, null);
			}
			countSize += 1;
			return new IndexNode(keyList, indexChildren, countSize, true);
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
				keyList2.add(firstChildren.get(k / 2).getKeys().get(0));
				firstChildren.remove(k / 2);
			}
			for (IndexNode in : firstChildren) {
				keyList1.add(in.getKeys().get(0));
			}
			keyList1.remove(0);
			keyList2.remove(0);
			IndexNode n = new IndexNode(keyList1, indexChildren, countSize, true);
			output.add(n);
			serializer.writeNextNode(n);
			countSize += 1;
			n = new IndexNode(keyList2, indexChildren, countSize, true);
			output.add(n);
			serializer.writeNextNode(n);

		} else if (!keyList.isEmpty()) {
			keyList.remove(0);
			countSize += 1;
			IndexNode n = new IndexNode(keyList, indexChildren, countSize, true);
			output.add(n);
			serializer.writeNextNode(n);
		}
		
		// recursion
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
